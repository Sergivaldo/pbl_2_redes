package br.uefs.local_server;

import br.uefs.dto.CarDTO;
import br.uefs.dto.GasStationDTO;
import br.uefs.mqtt.Listener;
import br.uefs.mqtt.MQTTClient;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

import static br.uefs.mqtt.Topics.*;

public class LocalServer extends Thread {
    private MQTTClient mqttClient;
    private Map<String, GasStationDTO> gasStations;


    public LocalServer(final MQTTClient mqttClient, final Map<String, GasStationDTO> gasStations) {
        this.mqttClient = mqttClient;
        this.gasStations = gasStations;
    }

    @Override
    public void run() {
        mqttClient.startOn();
        subscribeToTopics();
        listen();
    }

    private void listen() {
        while (true) ;
    }

    //GasStationDTO
    private static GasStationDTO requeredCentralServer(CarDTO car) throws IOException {
        String message = new Gson().toJson(car);

        Socket socket = new Socket("localhost", 9090);
        PrintStream exit = new PrintStream(socket.getOutputStream());

        exit.println(message);

        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
        BufferedReader reader = new BufferedReader(inputStream);
        String gasStation = reader.readLine();
        System.out.println("Recebeu do servidor: "+ gasStation);
        socket.close();

        GasStationDTO gasStationReceived = new Gson().fromJson(gasStation.toString(), GasStationDTO.class);
        return gasStationReceived;
    }

    private void subscribeToTopics() {
        //Tópico dos carros
        mqttClient.subscribe(CAR_REQUEST_RECHARGE.getValue(), new Listener(mqttMessage -> {
            Gson gson = new Gson();
            String payload = new String(mqttMessage.getPayload());
            CarDTO car = gson.fromJson(payload, CarDTO.class);
            GasStationDTO bestGasStation = selectBestGasStation(car,gasStations);
            String message = gson.toJson(bestGasStation);
            mqttClient.publish(CAR_RECEIVE_GAS_STATION.getValue() + car.getIdCar(), message.getBytes());
            double distance = getDistance(car.getCoordinates(),bestGasStation.getCoordinates());
            System.out.println("Carro "+car.getIdCar()+" foi direcionado para o posto: "+bestGasStation.getStationName()+"\nDistancia até o posto: "+distance+"\nTempo para chegar no posto:"+ (car.getTimePerKmTraveled() * distance)+"\n\n");
        }));

        //Tópicos dos postos
        mqttClient.subscribe(GAS_STATION_PUBLISH_STATUS.getValue(), new Listener(mqttMessage -> {
            String payload = new String(mqttMessage.getPayload());
            GasStationDTO gasStation = new Gson().fromJson(payload, GasStationDTO.class);
            gasStations.put(gasStation.getStationName(), gasStation);
            System.out.println("Posto "+gasStation.getStationName()+" se cadastrou\n"+"Postos cadastrados :"+gasStations.size()+"\n\n");
        }));
    }

    private static double getDistance(int[] coordinatesCar, int[] coordinatesGasStation) {
        double x0_x1 = Math.pow((coordinatesCar[0] - coordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((coordinatesCar[1] - coordinatesGasStation[1]), 2);
        return Math.sqrt(x0_x1 + y0_y1);
    }


    public static GasStationDTO selectBestGasStation(CarDTO car, Map<String,GasStationDTO> gasStations){
        GasStationDTO bestGasStation = null;
        double bestTime = 0;
        if(!gasStations.isEmpty()){
            //System.out.println(car.getDistanceForKMRateByPercentage()+" "+car.getCurrentBatteryCharge());
            float maximumDistance = car.getDistanceForKMRateByPercentage() * car.getCurrentBatteryCharge();
            Iterator<Map.Entry<String, GasStationDTO>> itr = gasStations.entrySet().iterator();
            while (itr.hasNext()) {
                GasStationDTO gasStation = itr.next().getValue();
                double distance = getDistance(car.getCoordinates(), gasStation.getCoordinates());
                //System.out.println(gasStation.getStationId());
                //System.out.println("Alcance do carro:"+maximumDistance+"   Distancia do posto:"+distance);
                if (maximumDistance >= distance) {
                    //System.out.println(gasStation.getStationId());
                    float waitingTime = gasStation.getCarsInQueue() * gasStation.getRechargeTime();
                    double time = waitingTime + (car.getTimePerKmTraveled() * distance);
                    //System.out.println("Tempo pra chegar no posto:"+time);
                    if (bestTime == 0 || time < bestTime) {
                        bestTime = time;
                        bestGasStation = gasStation;
                    }
                }
            }
        }
        //System.out.println("Aqui:"+bestTime);

        if(bestTime <= 350 && bestTime > 0){
            //System.out.println("Solicitação interna");
            return bestGasStation;// se o tempo de recarga do melhor posto local estiver dentro do aceitável
        }else{
            //System.out.println("solicitação externa");
            GasStationDTO newGasStation = null;
            try {
                newGasStation = requeredCentralServer(car);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(newGasStation != null){
                float timeWaiting = newGasStation.getCarsInQueue() * newGasStation.getRechargeTime();
                double distance = getDistance(car.getCoordinates(), newGasStation.getCoordinates());
                double centralBestTime = timeWaiting + (car.getTimePerKmTraveled() * distance);
                return (bestTime != 0)?((centralBestTime <= bestTime)? newGasStation: bestGasStation):newGasStation;
            }
            else{
                return bestGasStation;
            }
        }
    }
}

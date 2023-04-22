package br.uefs.local_server;

import br.uefs.central_server.CentralServerApplication;
import br.uefs.dto.CarDTO;
import br.uefs.dto.GasStationDTO;
import br.uefs.mqtt.Listener;
import br.uefs.mqtt.MQTTClient;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.*;
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
    private GasStationDTO requeredCentralServer(CarDTO car) throws IOException {
        String message = new Gson().toJson(car);

        Socket socket = new Socket("localhost", 9090);
        PrintStream exit = new PrintStream(socket.getOutputStream());

        exit.println(message);

        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
        BufferedReader reader = new BufferedReader(inputStream);
        String gasStation = reader.readLine();
        System.out.println("Recebeu do servidor "+ gasStation);
        socket.close();

        GasStationDTO gasStationReceived = new Gson().fromJson(gasStation.toString(), GasStationDTO.class);
        return gasStationReceived;
    }

    private void subscribeToTopics() {
        mqttClient.subscribe(CAR_REQUEST_RECHARGE.getValue(), new Listener(mqttMessage -> {
            Gson gson = new Gson();
            String payload = new String(mqttMessage.getPayload());
            CarDTO car = gson.fromJson(payload, CarDTO.class);
            GasStationDTO bestGasStation = selectBestGasStation(car,gasStations);
            String message = gson.toJson(bestGasStation);
            mqttClient.publish(CAR_RECEIVE_GAS_STATION.getValue() + car.getIdCar(), message.getBytes());
        }));

        mqttClient.subscribe(GAS_STATION_PUBLISH_STATUS.getValue(), new Listener(mqttMessage -> {
            String payload = new String(mqttMessage.getPayload());
            GasStationDTO gasStation = new Gson().fromJson(payload, GasStationDTO.class);
            gasStations.put(gasStation.getStationName(), gasStation);
        }));
    }

    private double getDistance(int[] coordinatesCar, int[] coordinatesGasStation) {
        double x0_x1 = Math.pow((coordinatesCar[0] - coordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((coordinatesCar[1] - coordinatesGasStation[1]), 2);
        return Math.sqrt(x0_x1 + y0_y1);
    }

    @SneakyThrows
    private GasStationDTO selectBestGasStation(CarDTO car, Map<String,GasStationDTO> gasStations){
        GasStationDTO bestGasStation = null;
        double bestTime = 0;
        if(!gasStations.isEmpty()){
            float maximumDistance = car.getDistanceForKMRateByPercentage() * car.getCurrentBatteryCharge();
            Iterator<Map.Entry<String, GasStationDTO>> itr = gasStations.entrySet().iterator();
            while (itr.hasNext()) {
                GasStationDTO gasStation = itr.next().getValue();
                double distance = getDistance(car.getCoordinates(), gasStation.getCoordinates());
                if (maximumDistance >= distance) {
                    float waitingTime = gasStation.getCarsInQueue() * gasStation.getRechargeTime();
                    double time = waitingTime + (car.getTimePerKmTraveled() * distance);
                    if (bestTime == 0 || time < bestTime) {
                        bestTime = time;
                        bestGasStation = gasStation;
                    }
                }
            }
        }
        if(bestTime <= 50 && bestTime > 0){
            return bestGasStation;// se o tempo de recarga do melhor posto local estiver dentro do aceitável
        }else{
            //requeredCentralServer(car):  pedir o melhor posto fora da nevoa para este carro
            GasStationDTO newGasStation = requeredCentralServer(car);
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

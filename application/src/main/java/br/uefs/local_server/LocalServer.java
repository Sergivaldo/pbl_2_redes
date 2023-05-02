package br.uefs.local_server;

import br.uefs.dto.CarDTO;
import br.uefs.dto.CentralServerDTO;
import br.uefs.dto.GasStationDTO;
import br.uefs.mqtt.Listener;
import br.uefs.mqtt.MQTTClient;
import br.uefs.utils.Mapper;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static br.uefs.mqtt.Topics.*;

public class LocalServer{
    @Getter
    private MQTTClient mqttClient;
    @Getter
    private Map<String, GasStationDTO> gasStations;
    private CentralServerDTO centralServer;
    private ScheduledExecutorService gasStationsExecutor = Executors.newSingleThreadScheduledExecutor();

    @Builder
    public LocalServer( MQTTClient mqttClient, Map<String, GasStationDTO> gasStations, CentralServerDTO centralServer) {
        this.mqttClient = mqttClient;
        this.gasStations = gasStations;
        this.centralServer = centralServer;
    }


    public void start() {
        mqttClient.startOn();
        subscribeToTopics();
        gasStationsExecutor.scheduleAtFixedRate(new SendGasStationsTask(),0,2, TimeUnit.SECONDS);
        listen();
    }

    private void listen() {
        while (true) ;
    }

    //GasStationDTO
    private GasStationDTO requeredCentralServer(CarDTO car) throws IOException, ClassNotFoundException {

        Socket socket = new Socket(centralServer.getHost(), centralServer.getPort());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(car);

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        socket.close();
        return (GasStationDTO) in.readObject();
    }

    private void subscribeToTopics() {
        //Tópico dos carros
        mqttClient.subscribe(CAR_REQUEST_RECHARGE.getValue(), new Listener(mqttMessage -> {
            Gson gson = new Gson();
            String payload = new String(mqttMessage.getPayload());
            CarDTO car = gson.fromJson(payload, CarDTO.class);
            GasStationDTO bestGasStation = selectBestGasStation(car, gasStations);
            String message = gson.toJson(bestGasStation);
            mqttClient.publish(CAR_RECEIVE_GAS_STATION.getValue() + car.getIdCar(), message.getBytes());
            double distance = getDistance(car.getCoordinates(), bestGasStation.getCoordinates());
            System.out.println("Carro " + car.getIdCar() + " foi direcionado para o posto: " + bestGasStation.getStationName() + "\nDistancia até o posto: " + distance + "\nTempo para chegar no posto:" + (car.getTimePerKmTraveled() * distance) + "\n\n");
        }));

        //Tópicos dos postos
        mqttClient.subscribe(GAS_STATION_PUBLISH_STATUS.getValue(), new Listener(mqttMessage -> {
            String payload = new String(mqttMessage.getPayload());
            GasStationDTO gasStation = new Gson().fromJson(payload, GasStationDTO.class);
            gasStations.put(gasStation.getStationName(), gasStation);
            System.out.println("Posto " + gasStation.getStationName() + " se cadastrou\n" + "Postos cadastrados :" + gasStations.size() + "\n\n");
        }));
    }

    private double getDistance(int[] coordinatesCar, int[] coordinatesGasStation) {
        double x0_x1 = Math.pow((coordinatesCar[0] - coordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((coordinatesCar[1] - coordinatesGasStation[1]), 2);
        return Math.sqrt(x0_x1 + y0_y1);
    }


    public GasStationDTO selectBestGasStation(CarDTO car, Map<String, GasStationDTO> gasStations) {
        GasStationDTO bestLocalGasStation = null;
        double localBestTime = -1;
        if (!gasStations.isEmpty()) {
            float maximumDistance = car.getDistanceForKMRateByPercentage() * car.getCurrentBatteryCharge();
            Iterator<Map.Entry<String, GasStationDTO>> itr = gasStations.entrySet().iterator();
            while (itr.hasNext()) {
                GasStationDTO gasStation = itr.next().getValue();
                double distance = getDistance(car.getCoordinates(), gasStation.getCoordinates());
                if (maximumDistance >= distance) {
                    float waitingTime = gasStation.getCarsInQueue() * gasStation.getRechargeTime();
                    double time = waitingTime + (car.getTimePerKmTraveled() * distance);

                    if (localBestTime == -1 || time < localBestTime) {
                        localBestTime = time;
                        bestLocalGasStation = gasStation;
                    }
                }
            }
        }

        if (localBestTime <= 350 && localBestTime > -1) {
            return bestLocalGasStation;
        } else {

            GasStationDTO bestExternalGasStation = null;
            try {
                bestExternalGasStation = requeredCentralServer(car);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (bestExternalGasStation != null) {
                float timeWaiting = bestExternalGasStation.getCarsInQueue() * bestExternalGasStation.getRechargeTime();
                double distance = getDistance(car.getCoordinates(), bestExternalGasStation.getCoordinates());
                double externalBestTime = timeWaiting + (car.getTimePerKmTraveled() * distance);
                return (localBestTime != -1) ? ((externalBestTime <= localBestTime) ? bestExternalGasStation : bestLocalGasStation) : bestExternalGasStation;
            } else {
                return bestLocalGasStation;
            }
        }
    }

    private LocalServer getLocalServer(){
        return this;
    }

    private class SendGasStationsTask implements Runnable{
        private Socket socket;
        private PrintWriter out;
        public SendGasStationsTask() {
            try {
                socket = new Socket(centralServer.getHost(),centralServer.getPort());
                out = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            out.print(Mapper.toLocalServerDTO(getLocalServer()));
        }
    }
}

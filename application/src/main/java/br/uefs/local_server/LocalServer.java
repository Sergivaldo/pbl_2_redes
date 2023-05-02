package br.uefs.local_server;

import br.uefs.dto.CarDTO;
import br.uefs.dto.CentralServerDTO;
import br.uefs.dto.GasStationDTO;
import br.uefs.mqtt.Listener;
import br.uefs.mqtt.MQTTClient;
import br.uefs.utils.Log;
import br.uefs.utils.Mapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static br.uefs.mqtt.Topics.*;

public class LocalServer {
    @Getter
    private String name;
    @Getter
    private MQTTClient mqttClient;
    @Getter
    private Map<String, GasStationDTO> gasStations;
    private CentralServerDTO centralServer;
    private ScheduledExecutorService gasStationsExecutor = Executors.newSingleThreadScheduledExecutor();

    @Builder
    public LocalServer(MQTTClient mqttClient, Map<String, GasStationDTO> gasStations, CentralServerDTO centralServer, String name) {
        this.mqttClient = mqttClient;
        this.gasStations = gasStations;
        this.centralServer = centralServer;
        this.name = name;
    }


    public void start() {
        mqttClient.startOn();
        subscribeToTopics();
        gasStationsExecutor.scheduleAtFixedRate(new SendGasStationsTask(), 0, 2, TimeUnit.SECONDS);
        listen();
    }

    private void listen() {
        while (true) ;
    }

    private void subscribeToTopics() {
        //Tópico dos carros
        mqttClient.subscribe(CAR_REQUEST_RECHARGE.getValue(), new Listener(mqttMessage -> {
            Gson gson = new Gson();
            String payload = new String(mqttMessage.getPayload());
            CarDTO car = gson.fromJson(payload, CarDTO.class);
            new BestGasStationProcessor(car).start();
        }));

        //Tópicos dos postos
        mqttClient.subscribe(GAS_STATION_PUBLISH_STATUS.getValue(), new Listener(mqttMessage -> {
            String payload = new String(mqttMessage.getPayload());
            GasStationDTO gasStation = new Gson().fromJson(payload, GasStationDTO.class);
            gasStations.put(gasStation.getStationName(), gasStation);
            System.out.println("Postos cadastrados :" + gasStations.size() + "\n\n");
        }));
    }

    private LocalServer getLocalServer() {
        return this;
    }

    private class SendGasStationsTask implements Runnable {

        @Override
        public void run() {
            try {
                Socket socket = new Socket(centralServer.getHost(), centralServer.getGasStationsReceiverPort());
                Log.success("Conectado ao servidor central");
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(Mapper.toLocalServerDTO(getLocalServer()));
                socket.close();
                System.out.println("Enviado");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @AllArgsConstructor
    private class BestGasStationProcessor extends Thread {
        private CarDTO car;

        public void run() {
            Gson gson = new Gson();
            GasStationDTO bestGasStation = selectBestGasStation(car, gasStations);
            String message = gson.toJson(bestGasStation);
            mqttClient.publish(CAR_RECEIVE_GAS_STATION.getValue() + car.getIdCar(), message.getBytes());
            if(bestGasStation != null){
                System.out.println("Carro " + car.getIdCar() + " foi direcionado para o posto: " + bestGasStation.getStationName() + "\n\n");
            }else{
                System.out.println("Não há postos disponíveis");
            }
        }

        private String requeredCentralServer(CarDTO car) throws IOException, ClassNotFoundException {

            Socket socket = new Socket(centralServer.getHost(), centralServer.getSolicitationCarReceiverPort());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(car);

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String gasStation = (String) in.readObject();
            System.out.println(gasStation);
            socket.close();
            return gasStation;
        }

        private GasStationDTO selectBestGasStation(CarDTO car, Map<String, GasStationDTO> gasStations) {
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
                System.out.println("Melhor posto local: " + bestLocalGasStation.getStationName());
                return bestLocalGasStation;
            } else {
                System.out.println("solicitação externa");
                GasStationDTO bestExternalGasStation = null;
                try {
                    String message = requeredCentralServer(car);
                    bestExternalGasStation = message.equals("null") ? null : new Gson().fromJson(message, GasStationDTO.class);
                    System.out.println(bestExternalGasStation != null ? "Melhor posto externo: " + bestExternalGasStation.getStationName() : "Não há postos externos disponíveis");
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

        private double getDistance(int[] coordinatesCar, int[] coordinatesGasStation) {
            double x0_x1 = Math.pow((coordinatesCar[0] - coordinatesGasStation[0]), 2);
            double y0_y1 = Math.pow((coordinatesCar[1] - coordinatesGasStation[1]), 2);
            return Math.sqrt(x0_x1 + y0_y1);
        }
    }
}

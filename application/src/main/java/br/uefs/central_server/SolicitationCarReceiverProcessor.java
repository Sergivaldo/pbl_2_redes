package br.uefs.central_server;

import br.uefs.dto.CarDTO;
import br.uefs.dto.CentralServerDTO;
import br.uefs.dto.GasStationDTO;
import br.uefs.dto.LocalServerDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class SolicitationCarReceiverProcessor extends Thread {

    private Socket receiverSocket;
    private CentralServerDTO centralServer;

    public SolicitationCarReceiverProcessor(Socket receiverSocket, CentralServerDTO centralServer) {
        this.receiverSocket = receiverSocket;
        this.centralServer = centralServer;
    }


    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(receiverSocket.getInputStream());
            CarDTO car = (CarDTO) in.readObject();
            System.out.println(car.getIdCar());
            ObjectOutputStream out = new ObjectOutputStream(receiverSocket.getOutputStream());
            out.writeObject(selectBestGasStation(car, centralServer.getLocalServers()));
            receiverSocket.close();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String selectBestGasStation(CarDTO car, List<LocalServerDTO> localServers) {
        Objects.requireNonNull(localServers);
        float maximumDistance = car.getDistanceForKMRateByPercentage() * car.getCurrentBatteryCharge();
        List<BestGasStation> bestGasStations = new ArrayList<>();
        for (LocalServerDTO localServer : localServers) {
            BestGasStation bestGasStation = null;
            Iterator<Map.Entry<String, GasStationDTO>> itr = localServer.getGasStations().entrySet().iterator();
            while (itr.hasNext()) {
                GasStationDTO gasStation = itr.next().getValue();
                double distance = getDistance(car.getCoordinates(), gasStation.getCoordinates());
                if (maximumDistance >= distance) {
                    float waitingTime = gasStation.getCarsInQueue() * gasStation.getRechargeTime();
                    double time = waitingTime + (car.getTimePerKmTraveled() * distance);
                    if (time < bestGasStation.getTime()) {
                        bestGasStation.setTime(time);
                        bestGasStation.setGasStation(gasStation);
                    }
                }
            }
            bestGasStations.add(bestGasStation);
        }
        Comparator<BestGasStation> comparator = Comparator.comparing(BestGasStation::getTime);
        Optional<BestGasStation> result = bestGasStations.stream().min(comparator);
        Gson gson = new Gson();
        String gasStation;
        if(result.isPresent()){
            gasStation = gson.toJson(result.get().gasStation);
            System.out.println("Melhor posto externo: "+result.get().getGasStation().getStationName());
        }else{
            System.out.println("Não há postos disponíveis");
            gasStation = "null";
        }

        return gasStation;
    }

    private double getDistance(int[] coordinatesCar, int[] coordinatesGasStation) {
        double x0_x1 = Math.pow((coordinatesCar[0] - coordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((coordinatesCar[1] - coordinatesGasStation[1]), 2);
        return Math.sqrt(x0_x1 + y0_y1);
    }

    @Setter
    @Getter
    private class BestGasStation {
        private double time;
        private GasStationDTO gasStation;
    }
}

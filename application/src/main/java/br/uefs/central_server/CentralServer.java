package br.uefs.central_server;

import br.uefs.dto.CarDTO;
import br.uefs.dto.GasStationDTO;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

public class CentralServer extends Thread{
    private Socket socket;
    private Map<String, GasStationDTO> gasStations;

    public CentralServer(Socket socket){
        this.socket = socket;
    }

    private void receiveGasStations(CarDTO car) throws IOException {
        for(int i = 0; i < CentralServerApplication.cloudPorts.size(); i++) {
            Socket socket = new Socket("localhost", CentralServerApplication.cloudPorts.get(i));
            PrintStream exit = new PrintStream(socket.getOutputStream());

            exit.println(new Gson().toJson(car));

            InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
            BufferedReader reader = new BufferedReader(inputStream);
            String jsonGasStation = reader.readLine();
            System.out.println("Recebeu do servidor local: " + jsonGasStation);
            GasStationDTO gasStation = new Gson().fromJson(jsonGasStation, GasStationDTO.class);
            gasStations.put(gasStation.getStationName(),gasStation);
            socket.close();
        }
    }

    @Override
    public void run(){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader messageReader = new BufferedReader(inputStreamReader);
            String message = messageReader.readLine();
            System.out.println("Recebeu do cliente" + message);
            CarDTO car = new Gson().fromJson(messageReader, CarDTO.class);
            receiveGasStations(car);

            PrintStream outMessage = new PrintStream(socket.getOutputStream());
            outMessage.println(new Gson().toJson(selectBestGasStation(car, gasStations)));

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private GasStationDTO selectBestGasStation(CarDTO car, Map<String,GasStationDTO> gasStations) {
        if (gasStations.isEmpty() == true) {
            return null;
        }
        GasStationDTO bestGasStation = null;
        double bestTime = 0;
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
        return bestGasStation;
    }

    private double getDistance(int[] coordinatesCar, int[] coordinatesGasStation) {
        double x0_x1 = Math.pow((coordinatesCar[0] - coordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((coordinatesCar[1] - coordinatesGasStation[1]), 2);
        return Math.sqrt(x0_x1 + y0_y1);
    }
}

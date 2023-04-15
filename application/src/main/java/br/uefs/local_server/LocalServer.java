package br.uefs.local_server;

import br.uefs.dto.CarDTO;
import br.uefs.gas_station.GasStation;
import br.uefs.dto.GasStationDTO;
import br.uefs.mqtt.MQTTClient;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Iterator;
import java.util.Map;

import static br.uefs.mqtt.Topics.*;

public class LocalServer extends Thread{
    private MQTTClient mqttClient;
    private Map<String, GasStationDTO> gasStations;

    public LocalServer(final MQTTClient mqttClient, final Map<String, GasStationDTO> gasStations) {
        this.mqttClient = mqttClient;
        this.gasStations = gasStations;
    }

    @Override
    public void run(){
        mqttClient.startOn();
        subscribeToTopics();
        listen();
    }

    private void listen(){
        while (true);
    }

    private void subscribeToTopics() {
        mqttClient.subscribe(CAR_REQUEST_RECHARGE.getValue(), (s, mqttMessage) -> {
            Gson gson = new Gson();
            CarDTO car = gson.fromJson(new String(mqttMessage.getPayload()), CarDTO.class);
            String message = gson.toJson(selectBestGasStation(car, gasStations));
            System.out.println(message);
            mqttClient.publish(CAR_RECEIVE_GAS_STATION.getValue() + car.getIdCar(), message.getBytes(), 0);
        });

        mqttClient.subscribe(GAS_STATION_PUBLISH_STATUS.getValue(), (s, mqttMessage) -> {
            GasStationDTO gasStation = new Gson().fromJson(new String(mqttMessage.getPayload()), GasStationDTO.class);
            gasStations.put(gasStation.getStationName(), gasStation);
            System.out.println(gasStation);
        });
    }

    private double getDistance(int[] coordinatesCar, int[] coordinatesGasStation) {
        double x0_x1 = Math.pow((coordinatesCar[0] - coordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((coordinatesCar[1] - coordinatesGasStation[1]), 2);
        return Math.pow((x0_x1 + y0_y1), 0.5);
    }

    private GasStation selectBestGasStation(CarDTO car, Map gasStations) {
        GasStation bestGasStation = null;
        double bestTime = 0;
        float maximumDistance = car.getDistanceForKMRateByPercentage() * car.getCurrentBatteryCharge();

        Iterator<Map.Entry<String, GasStation>> itr = gasStations.entrySet().iterator();
        while (itr.hasNext()) {
            GasStation gasStation = itr.next().getValue();
            double distance = getDistance(car.getCoordinates(), gasStation.getCoordinates());
            if (maximumDistance >= distance) {
                float waitingTime = gasStation.getCarsInLine() * gasStation.getRechargeTime();
                double time = waitingTime + (car.getTimePerKmTraveled() * distance);
                if (bestTime == 0 || time < bestTime) {
                    bestTime = time;
                    bestGasStation = gasStation;
                }
            }
            System.out.println();
        }
        return bestGasStation;
    }


}

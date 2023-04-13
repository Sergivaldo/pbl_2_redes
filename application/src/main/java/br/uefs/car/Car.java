package br.uefs.car;

import br.uefs.gas_station.GasStation;
import br.uefs.mqtt.MQTTClient;
import br.uefs.utils.Mapper;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static br.uefs.mqtt.Topics.CAR_RECEIVE_GAS_STATION;
import static br.uefs.mqtt.Topics.CAR_REQUEST_RECHARGE;

@Getter
@Setter
public class Car {

    private int[] coordinates;
    private String idCar;
    private float distanceForKMRateByPercentage;
    private float timePerKmTraveled;
    private final BatteryChecker batteryChecker = new BatteryChecker();
    private final Battery battery = new Battery();
    private GasStation bestGasStation = null;
    private MQTTClient MQTTClient;


    public Car(int[] coordinates, String idCar, float distanceForKMRateByPercentage, float timePerKmTraveled, MQTTClient mqttClient) {
        this.MQTTClient = mqttClient;
        this.coordinates = coordinates;
        this.idCar = idCar;
        this.distanceForKMRateByPercentage = distanceForKMRateByPercentage;
        this.timePerKmTraveled = timePerKmTraveled;
        batteryChecker.start();
        battery.start();
        mqttClient.startOn();
        MQTTClient.subscribe(CAR_RECEIVE_GAS_STATION.getValue() + idCar, new IMqttMessageListener() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                bestGasStation = new Gson().fromJson(new String(mqttMessage.getPayload()), GasStation.class);
            }
        });

    }

    public Car getCar() {
        return this;
    }

    private class BatteryChecker extends Thread {
        private int checkInterval = 2000;

        @Override
        public void run() {
            try {

                while (true) {
                    Thread.sleep(checkInterval);
                    if (battery.currentCharge <= 30) {
                        Gson gson = new Gson();
                        String message = gson.toJson(Mapper.toCarReference(getCar()));
                        System.out.println(message);
                        MQTTClient.publish(CAR_REQUEST_RECHARGE.getValue(), message.getBytes(), 0);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Getter
    public class Battery extends Thread {
        private int currentCharge = 100;
        private int speedDischarge = 2;

        public void discharge() {
            currentCharge -= currentCharge > 0 ? 5 : 0;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(speedDischarge * 1000);
                    discharge();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

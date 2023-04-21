package br.uefs.car;

import br.uefs.gas_station.GasStation;
import br.uefs.mqtt.MQTTClient;
import br.uefs.utils.Mapper;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static br.uefs.mqtt.Topics.CAR_RECEIVE_GAS_STATION;
import static br.uefs.mqtt.Topics.CAR_REQUEST_RECHARGE;

@Getter
@Setter
public class Car {

    private int[] coordinates;
    private String idCar;
    private float distanceByBatteryPercent;
    private float timePerDistanceTraveled;
    private final ScheduledExecutorService batteryCheckerExecutor = Executors.newSingleThreadScheduledExecutor();
    private Battery battery;
    private GasStation bestGasStation;
    private MQTTClient mqttClient;

    @Builder
    public Car(int[] coordinates, String idCar, float distanceByBatteryPercent, float timePerDistanceTraveled, MQTTClient mqttClient) {
        this.mqttClient = mqttClient;
        this.coordinates = coordinates;
        this.idCar = idCar;
        this.distanceByBatteryPercent = distanceByBatteryPercent;
        this.timePerDistanceTraveled = timePerDistanceTraveled;
    }

    public void start() {
        mqttClient.startOn();
        subscribeToTopic();
        battery = new Battery();
        batteryCheckerExecutor.scheduleAtFixedRate(new BatteryChecker(), 0, 2, TimeUnit.SECONDS);
    }

    private void subscribeToTopic() {
        mqttClient.subscribe(CAR_RECEIVE_GAS_STATION.getValue() + idCar, (s, mqttMessage)
                -> bestGasStation = new Gson().fromJson(new String(mqttMessage.getPayload()), GasStation.class));

    }

    public Car getCar() {
        return this;
    }

    private class BatteryChecker implements Runnable {
        @Override
        public void run() {
            if (battery.currentCharge <= 30) {
                Gson gson = new Gson();
                String message = gson.toJson(Mapper.toCarDTO(getCar()));
                mqttClient.publish(CAR_REQUEST_RECHARGE.getValue(), message.getBytes());
            }
        }
    }

    @Getter
    public class Battery {
        private int currentCharge = 100;
        private int dischargeRate;
        @Getter(AccessLevel.NONE)
        private final ScheduledExecutorService dischargeExecutor = Executors.newSingleThreadScheduledExecutor();

        @Getter(AccessLevel.NONE)
        private final ScheduledExecutorService updateRateExecutor = Executors.newSingleThreadScheduledExecutor();

        private ScheduledFuture dischargeTask;

        public Battery() {
            updateRateExecutor.scheduleAtFixedRate(new UpdateDischargeRateTask(), 0, 10, TimeUnit.SECONDS);
        }

        private class UpdateDischargeRateTask implements Runnable {
            @Override
            public void run() {
                int[] rates = {3, 5, 7};
                int nextDischargeRate = new Random().nextInt(rates.length);
                dischargeRate = rates[nextDischargeRate];
                if (dischargeTask != null) {
                    dischargeTask.cancel(true);
                }
                System.out.print("\r\uD83E\uDEAB " + currentCharge + "% ↓(" + dischargeRate + "s)           ");
                dischargeTask = dischargeExecutor.scheduleAtFixedRate(new DischargeTask(), dischargeRate, dischargeRate, TimeUnit.SECONDS);
            }
        }

        private class DischargeTask implements Runnable {

            @Override
            public void run() {
                currentCharge -= currentCharge > 0 ? 5 : 0;
                System.out.print("\r\uD83E\uDEAB " + currentCharge + "% ↓(" + dischargeRate + "s)          ");
            }
        }
    }

}

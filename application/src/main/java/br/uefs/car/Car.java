package br.uefs.car;

import br.uefs.dto.GasStationDTO;
import br.uefs.mqtt.Listener;
import br.uefs.mqtt.MQTTClient;
import br.uefs.utils.Mapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sergivaldo.framework.FrameworkApplication;
import sergivaldo.framework.http.HttpRoute;
import sergivaldo.framework.http.HttpRouteMethod;
import sergivaldo.framework.http.HttpRouter;
import sergivaldo.framework.http.message.HttpMessageHandler;
import sergivaldo.framework.http.message.HttpMessageParser;
import sergivaldo.framework.http.message.request.DefaultHttpMethods;
import sergivaldo.framework.http.message.response.HttpResponse;
import sergivaldo.framework.http.message.response.HttpStatus;
import sergivaldo.framework.socket.Configuration;

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
    private Battery battery = new Battery();
    private GasStationDTO bestGasStation;
    private MQTTClient mqttClient;
    private CarInterface carInterface;

    @Builder
    public Car(int[] coordinates, String idCar, float distanceByBatteryPercent, float timePerDistanceTraveled, MQTTClient mqttClient,int interfacePort,String interfaceHost) {
        this.mqttClient = mqttClient;
        this.coordinates = coordinates;
        this.idCar = idCar;
        this.distanceByBatteryPercent = distanceByBatteryPercent;
        this.timePerDistanceTraveled = timePerDistanceTraveled;
        this.carInterface = new CarInterface(interfacePort,interfaceHost);
    }

    public void start() {
        mqttClient.startOn();
        subscribeToTopic();
        battery.start();
        batteryCheckerExecutor.scheduleAtFixedRate(new BatteryChecker(), 0, 2, TimeUnit.SECONDS);
        carInterface.start();
    }

    private void subscribeToTopic() {
        mqttClient.subscribe(CAR_RECEIVE_GAS_STATION.getValue() + idCar, new Listener((mqttMessage)
                -> bestGasStation = new Gson().fromJson(new String(mqttMessage.getPayload()), GasStationDTO.class)));
    }

    public Car getCar() {
        return this;
    }

    private class BatteryChecker implements Runnable {
        @Override
        public void run() {
            if (battery.currentCharge <= 30 && battery.currentCharge > 0) {
                Gson gson = new Gson();
                String message = gson.toJson(Mapper.toCarDTO(getCar()));
                mqttClient.publish(CAR_REQUEST_RECHARGE.getValue(), message.getBytes());
            }
        }
    }

    @Getter
    public class Battery {
        @Setter
        private int currentCharge;
        private int dischargeRate;
        @Getter(AccessLevel.NONE)
        private final ScheduledExecutorService dischargeExecutor = Executors.newSingleThreadScheduledExecutor();

        @Getter(AccessLevel.NONE)
        private final ScheduledExecutorService updateRateExecutor = Executors.newSingleThreadScheduledExecutor();

        private ScheduledFuture dischargeTask;

        public void start(){
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
                System.out.print("bateria -> " + currentCharge + "% (" + dischargeRate + "s)\r");
                dischargeTask = dischargeExecutor.scheduleAtFixedRate(new DischargeTask(), dischargeRate, dischargeRate, TimeUnit.SECONDS);
            }
        }

        private class DischargeTask implements Runnable {
            @Override
            public void run() {
                currentCharge -= currentCharge > 0 ? 5 : 0;
                System.out.print("bateria -> " + currentCharge + "% (" + dischargeRate + "s)\r");
            }
        }
    }

    private class CarInterface {
        private int interfacePort;
        private String interfaceHost;

        public CarInterface(int interfacePort,String interfaceHost) {
            this.interfacePort = interfacePort;
            this.interfaceHost = interfaceHost;
        }

        public void start() {
            HttpRouter router = new HttpRouter();
            HttpRoute route = new HttpRoute("/best_gas_station");
            HttpRouteMethod httpRouteMethod = new HttpRouteMethod(DefaultHttpMethods.GET, r -> {
                HttpResponse response;
                if (bestGasStation != null) {
                    response = new HttpResponse(HttpStatus.OK, bestGasStation.toString());
                } else {
                    JsonObject json = new JsonObject();
                    json.addProperty("message", "Gas station not available");
                    response = new HttpResponse(HttpStatus.OK, json.toString());
                }

                return response;
            });
            route.addMethod(httpRouteMethod);
            router.addRoute(route);
            Configuration config = new Configuration();

            config.setServerPort(interfacePort);
            config.setServerHost(interfaceHost);
            FrameworkApplication api = new FrameworkApplication(
                    config,
                    router,
                    new HttpMessageHandler(),
                    new HttpMessageParser()
            );
            api.start();
        }
    }

}

package br.uefs.gas_station;

import br.uefs.mqtt.MQTTClient;
import br.uefs.utils.Mapper;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static br.uefs.mqtt.Topics.GAS_STATION_PUBLISH_STATUS;

@Getter
@Setter
public class GasStation {
    private int[] coordinates;
    private String stationName;
    private String stationId;
    private int carsInQueue;
    private int rechargeTime;
    private MQTTClient mqttClient;
    private ScheduledExecutorService sendMessageExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService uploadSizeQueueExecutor = Executors.newSingleThreadScheduledExecutor();

    @Builder
    public GasStation(int[] coordinates, String name, String stationId, int carsInQueue, int rechargeTime, MQTTClient mqttClient) {
        this.coordinates = coordinates;
        this.stationName = name;
        this.stationId = stationId;
        this.carsInQueue = carsInQueue;
        this.rechargeTime = rechargeTime;
        this.mqttClient = mqttClient;
    }

    public void start() {
        mqttClient.startOn();
        sendMessageExecutor.scheduleAtFixedRate(new SendMessageTask(), 0, 2, TimeUnit.SECONDS);
        sendMessageExecutor.scheduleAtFixedRate(new UploadSizeQueueTask(), 0, 20, TimeUnit.SECONDS);
    }

    private GasStation getGasStation() {
        return this;
    }

    public class SendMessageTask implements Runnable {
        private final Gson gson = new Gson();

        @Override
        public void run() {
            String message = gson.toJson(Mapper.toGasStationDTO(getGasStation()));
            mqttClient.publish(GAS_STATION_PUBLISH_STATUS.getValue(), message.getBytes());
        }
    }

    public class UploadSizeQueueTask implements Runnable {
        @Override
        public void run() {
            carsInQueue = new Random().nextInt(16);
            carsInQueue = carsInQueue > 0 ? carsInQueue : 2;
            System.out.println("Carros na fila: "+carsInQueue);
        }
    }
}

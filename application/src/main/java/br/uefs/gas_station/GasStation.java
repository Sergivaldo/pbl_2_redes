package br.uefs.gas_station;

import br.uefs.mqtt.MQTTClient;
import br.uefs.utils.Mapper;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import static br.uefs.mqtt.Topics.GAS_STATION_PUBLISH_STATUS;

@Getter
@Setter
public class GasStation extends Thread{
    private int[] coordinates;
    private String stationName;
    private String stationId;
    private int carsInLine;
    private int rechargeTime;
    private int updateTime = 1000;
    private MQTTClient mqttClient;

    public GasStation(int[] coordinates, String name, String stationId, int carsInLine, int rechargeTime, MQTTClient mqttClient) {
        this.coordinates = coordinates;
        this.stationName = name;
        this.stationId = stationId;
        this.carsInLine = carsInLine;
        this.rechargeTime = rechargeTime;
        this.mqttClient = mqttClient;
        mqttClient.startOn();
    }
    @Override
    public void run(){
        Gson gson = new Gson();
        while(true){
            try {
                Thread.sleep(updateTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String message = gson.toJson(Mapper.toGasStationDTO(this));
            System.out.println(message);
            mqttClient.publish(GAS_STATION_PUBLISH_STATUS.getValue(), message.getBytes(), 0);
        }
    }
}

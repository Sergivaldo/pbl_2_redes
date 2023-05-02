package br.uefs.dto;

import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class GasStationDTO implements Serializable {
    private int[] coordinates;
    private String stationName;
    private String stationId;
    private int carsInQueue;
    private int rechargeTime;

    public GasStationDTO(int[] coordinates, String stationName, String stationId, int carsInQueue, int rechargeTime) {
        this.coordinates = coordinates;
        this.stationName = stationName;
        this.stationId = stationId;
        this.carsInQueue = carsInQueue;
        this.rechargeTime = rechargeTime;
    }

    @Override
    public String toString(){
        JsonObject json = new JsonObject();
        json.addProperty("id", stationId);
        json.addProperty("name", stationName);
        json.addProperty("cars_in_queue", carsInQueue);

        return json.toString();
    }
}

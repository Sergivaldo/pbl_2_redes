package br.uefs.gas_station;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GasStation {
    private int[] coordinates;
    private String name;
    private String id;
    private int carsInLine;
    private int currentRechargeTime;

    private GasStation(int[] coordinates, String name, String id, int carsInLine, int currentRechargeTime) {
        this.coordinates = coordinates;
        this.name = name;
        this.id = id;
        this.carsInLine = carsInLine;
        this.currentRechargeTime = currentRechargeTime;
    }

    public float getWaitingTime(){
        return carsInLine * currentRechargeTime;
    }
}

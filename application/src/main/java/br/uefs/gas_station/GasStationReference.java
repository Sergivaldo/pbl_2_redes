package br.uefs.gas_station;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GasStationReference {
    private int[] coordinates;
    private String stationName;
    private String stationId;
    private int carsInLine;
    private int rechargeTime;

    public GasStationReference(int[] coordinates, String stationName, String stationId, int carsInLine, int rechargeTime) {
        this.coordinates = coordinates;
        this.stationName = stationName;
        this.stationId = stationId;
        this.carsInLine = carsInLine;
        this.rechargeTime = rechargeTime;
    }
}

package br.uefs.car;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarReference {
    private int[] coordinates;
    private String idCar;
    private float distanceForKMRateByPercentage;
    private float timePerKmTraveled;
    private float currentBatteryCharge;
}

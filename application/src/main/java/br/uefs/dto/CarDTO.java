package br.uefs.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarDTO {
    private int[] coordinates;
    private String idCar;
    private float distanceForKMRateByPercentage;
    private float timePerKmTraveled;
    private float currentBatteryCharge;
}

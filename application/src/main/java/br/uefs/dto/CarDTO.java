package br.uefs.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class CarDTO implements Serializable {
    private int[] coordinates;
    private String idCar;
    private float distanceForKMRateByPercentage;
    private float timePerKmTraveled;
    private float currentBatteryCharge;
}

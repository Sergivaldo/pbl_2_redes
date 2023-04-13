package br.uefs.car;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Car {
    private int[] coordinates;
    private String idCar;
    private int battery;
    private float distanceForKMRateByPercentage;
    private float timePerKmTraveled;
    private final BatteryChecker batteryChecker = new BatteryChecker();

    public Car(int[] coordinates, String idCar, int battery, float distanceForKMRateByPercentage, float timePerKmTraveled) {
        this.coordinates = coordinates;
        this.idCar = idCar;
        this.battery = battery;
        this.distanceForKMRateByPercentage = distanceForKMRateByPercentage;
        this.timePerKmTraveled = timePerKmTraveled;
    }

    public float getMaximumDistanceHit() {
        return this.distanceForKMRateByPercentage * battery;
    }

    public class BatteryChecker extends Thread{
        private int checkInterval = 2000;
        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(checkInterval);
                    if (battery <= 30) {

                        // Selecionar um posto usando a classe SelectGasStation
                    }
                }
            }catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

}

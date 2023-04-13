package br.uefs.car;

import br.uefs.gas_station.GasStation;

public class CarApplication {
    public static Car car;
    public static GasStation bestGasStation;
    public static void main(String[] args) {

        // Gerar esses dados aleatoriamente
        int[] location = {100, 22};
        String idCar = "0001";
        int battery = 100;
        float distanceForKMRateByPercentage = 4;
        float timePerKmTraveled = 2;

        car = new Car(location,idCar,battery,distanceForKMRateByPercentage,timePerKmTraveled);
    }
}

package br.uefs.car;

import br.uefs.gas_station.GasStation;
import br.uefs.mqtt.MQTTClient;

public class CarApplication {
    public static Car car;
    public static GasStation bestGasStation;

    public static void main(String[] args) {

        // Gerar esses dados aleatoriamente
        int[] location = {100, 22};
        String idCar = "0001";
        float distanceForKMRateByPercentage = 4;
        float timePerKmTraveled = 2;

        MQTTClient MQTTClient = new MQTTClient("tcp://127.0.0.1:1883", null, null);
        car = new Car(location, idCar, distanceForKMRateByPercentage, timePerKmTraveled, MQTTClient);

    }
}

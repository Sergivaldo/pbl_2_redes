package br.uefs.car;

import br.uefs.gas_station.GasStation;

import java.util.Iterator;
import java.util.Map;

public class SelectGasStation {
    public static double getDistance(int[] coordinatesCar, int[] coordinatesGasStation) {
        double x0_x1 = Math.pow((coordinatesCar[0] - coordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((coordinatesCar[1] - coordinatesGasStation[1]), 2);
        return Math.pow((x0_x1 + y0_y1), 0.5);
    }

    public static GasStation selectBestGasStation(Car car, Map gasStations) {
        GasStation bestGasStation = null;
        double bestTime = 0;
        float maximumDistance = car.getMaximumDistanceHit();

        Iterator<Map.Entry<String, GasStation>> itr = gasStations.entrySet().iterator();
        while (itr.hasNext()) {
            GasStation gasStation = itr.next().getValue();
            double distance = getDistance(car.getCoordinates(), gasStation.getCoordinates());
            if (maximumDistance >= distance) {
                double time = gasStation.getWaitingTime() + (car.getTimePerKmTraveled() * distance);
                if (bestTime == 0 || time < bestTime) {
                    bestTime = time;
                    bestGasStation = gasStation;
                }
            }
            System.out.println();
        }
        return bestGasStation;
    }
}

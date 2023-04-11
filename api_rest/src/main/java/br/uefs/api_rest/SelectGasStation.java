package br.uefs.api_rest;

import java.util.Iterator;
import java.util.Map;

public class SelectGasStation {
    public static double getDistance(int[]locationCoordinatesCar, int[] locationCoordinatesGasStation){
        double x0_x1 = Math.pow((locationCoordinatesCar[0] - locationCoordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((locationCoordinatesCar[1] - locationCoordinatesGasStation[1]), 2);
        return Math.pow((x0_x1 + y0_y1),0.5);
    }

    public static GasStation selectBestGasStation(Car car, Map gasStations){
        GasStation bestGasStation = null;
        double bestTime = 0;
        float maximumDistance = car.getMaximumDistanceHit();

        Iterator<Map.Entry<String, GasStation>> itr = gasStations.entrySet().iterator();
        while (itr.hasNext()) {
            GasStation gasStation = itr.next().getValue();
            double distance = getDistance(car.getLocationCoordinatesCar(), gasStation.getLocationCoordinatesGasStation());
            if(maximumDistance >= distance) {
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

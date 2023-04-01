package br.uefs.api_rest;

public class SelectGasStation {
    private double getDistance(int[]locationCoordinatesCar, int[] locationCoordinatesGasStation){
        double x0_x1 = Math.pow((locationCoordinatesCar[0] - locationCoordinatesGasStation[0]), 2);
        double y0_y1 = Math.pow((locationCoordinatesCar[1] - locationCoordinatesGasStation[1]), 2);
        return Math.pow((x0_x1 + y0_y1),0.5);
    }

    public GasStation selectBestGasStation(Car car, GasStation[] gasStations){
        GasStation bestGasStation = null;
        double bestTime = 0;
        float maximumDistance = car.getMaximumDistanceHit();

        for(int i = 0; i<gasStations.length;i++){
            double distance = getDistance(car.getLocationCoordinatesCar(), gasStations[i].getLocationCoordinatesGasStation());
            if(maximumDistance >= distance) {
                double time = gasStations[i].getWaitingTime() + (car.getTimePerKmTraveled() * distance);
                if (bestTime != 0 || time < bestTime) {
                    bestTime = time;
                    bestGasStation = gasStations[i];
                }
            }
        }
        return bestGasStation;
    }
}

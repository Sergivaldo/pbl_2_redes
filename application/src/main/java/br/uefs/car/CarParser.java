package br.uefs.car;

import br.uefs.exceptions.InvalidChargeBatteryException;
import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.Log;

import java.util.List;
import java.util.Objects;

public class CarParser {

    public static Car parseCar(List<String> properties) {
        Objects.requireNonNull(properties);
        Car newCar = null;
        try {
            newCar = new Car.CarBuilder()
                    .idCar(parseId(properties))
                    .distanceByBatteryPercent(parseDistanceByBatteryPercent(properties))
                    .coordinates(parseCoordinates(properties))
                    .timePerDistanceTraveled(parseTimePerDistanceTraveled(properties))
                    .build();
            newCar.getBattery().setCurrentCharge(parseCurrentBatteryCharge(properties));
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        } catch (InvalidChargeBatteryException e) {
            e.printStackTrace();
        }
        return newCar;
    }

    private static String parseId(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-i");
        if (indexProperty != -1) {
            return properties.get(indexProperty + 1);
        } else {
            throw new NoSuchPropertyException("-i property not found");
        }
    }

    private static int[] parseCoordinates(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-c");
        if (indexProperty != -1) {
            String[] coordinates = properties.get(indexProperty + 1).split(",");
            return new int[]{Integer.parseInt(coordinates[0].replace("[","")), Integer.parseInt(coordinates[1].replace("]",""))};

        } else {
            throw new NoSuchPropertyException("-c property not found");
        }
    }

    private static float parseDistanceByBatteryPercent(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-d");
        if (indexProperty != -1) {
            return Float.parseFloat(properties.get(indexProperty + 1));
        } else {
            throw new NoSuchPropertyException("-d property not found");
        }
    }

    private static float parseTimePerDistanceTraveled(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-t");
        if (indexProperty != -1) {
            return Float.parseFloat(properties.get(indexProperty + 1));
        } else {
            throw new NoSuchPropertyException("-t property not found");
        }
    }

    private static int parseCurrentBatteryCharge(List<String> properties) throws NoSuchPropertyException, InvalidChargeBatteryException {
        int indexProperty = properties.indexOf("-b");
        if (indexProperty != -1) {
            int value = Integer.parseInt(properties.get(indexProperty + 1));
            if(value >= 0 && value <= 100){
                return value;
            }else{
                throw new InvalidChargeBatteryException("value not acceptable");
            }
        } else {
            throw new NoSuchPropertyException("-b property not found");
        }
    }

}

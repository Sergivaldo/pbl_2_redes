package br.uefs.gas_station;

import br.uefs.exceptions.NoSuchPropertyException;

import java.util.List;
import java.util.Objects;

public class GasStationParser {

    public static GasStation parseGasStation(List<String> properties){
        Objects.requireNonNull(properties);
        GasStation newGasStation = null;
        try {
            newGasStation = GasStation.builder()
                    .name(parseName(properties))
                    .carsInLine(parseCarsInLine(properties))
                    .coordinates(parseCoordinates(properties))
                    .stationId(parseStationId(properties))
                    .rechargeTime(parseRechargeTime(properties))
                    .build();
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }

        return newGasStation;
    }

    private static int parseRechargeTime(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-d");
        if (indexProperty != -1) {
            return Integer.parseInt(properties.get(indexProperty + 1));
        } else {
            throw new NoSuchPropertyException("-d property not found");
        }
    }

    private static String parseStationId(List<String> properties) throws NoSuchPropertyException {
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

    private static int parseCarsInLine(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-d");
        if (indexProperty != -1) {
            return Integer.parseInt(properties.get(indexProperty + 1));
        } else {
            throw new NoSuchPropertyException("-d property not found");
        }
    }

    private static String parseName(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-i");
        if (indexProperty != -1) {
            return properties.get(indexProperty + 1);
        } else {
            throw new NoSuchPropertyException("-i property not found");
        }
    }
}

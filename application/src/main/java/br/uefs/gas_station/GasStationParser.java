package br.uefs.gas_station;

import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.PropertiesParser;

import java.util.List;
import java.util.Objects;

public class GasStationParser {

    public static GasStation parseGasStation(List<String> properties) {
        Objects.requireNonNull(properties);
        GasStation newGasStation = null;
        PropertiesParser parser = new PropertiesParser(properties);
        try {
            newGasStation = GasStation.builder()
                    .name(parser.parseString("-n"))
                    .carsInQueue(parser.parseInt("-q"))
                    .coordinates(parser.parseIntArray("-c"))
                    .stationId(parser.parseString("-i"))
                    .rechargeTime(parser.parseInt("-r"))
                    .build();
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }

        return newGasStation;
    }
}
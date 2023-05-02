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
                    .name(parser.parseString("-name"))
                    .carsInQueue(parser.parseInt("-queue"))
                    .coordinates(parser.parseIntArray("-coordinates"))
                    .stationId(parser.parseString("-id"))
                    .rechargeTime(parser.parseInt("-recharge_time"))
                    .build();
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }

        return newGasStation;
    }
}
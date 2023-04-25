package br.uefs.central_server;

import br.uefs.car.Car;
import br.uefs.exceptions.InvalidChargeBatteryException;
import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.PropertiesParser;

import java.util.List;
import java.util.Objects;

public class CentralServerParser {
    public static void parseListPorts(List<String> properties) {
        Objects.requireNonNull(properties);
        PropertiesParser parser = new PropertiesParser(properties);
        try {
            CentralServerApplication.cloudPorts = parser.parseIntArray("-ports");
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
    }

}

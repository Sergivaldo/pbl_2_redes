package br.uefs.car;

import br.uefs.exceptions.InvalidChargeBatteryException;
import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.PropertiesParser;

import java.util.List;
import java.util.Objects;

public class CarParser {

    public static Car parseCar(List<String> properties) {
        Objects.requireNonNull(properties);
        Car newCar = null;
        PropertiesParser parser = new PropertiesParser(properties);
        try {
            newCar = new Car.CarBuilder()
                    .idCar(parser.parseString("-i"))
                    .distanceByBatteryPercent(parser.parserFloat("-d"))
                    .coordinates(parser.parseIntArray("-c"))
                    .timePerDistanceTraveled(parser.parserFloat("-t"))
                    .build();
            int batteryCharge = parser.parseInt("-b");
            if (batteryCharge >= 0 && batteryCharge <= 100) {
                newCar.getBattery().setCurrentCharge(batteryCharge);
            }else{
                throw new InvalidChargeBatteryException("battery charge value not acceptable");
            }
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        } catch (InvalidChargeBatteryException e) {
            e.printStackTrace();
        }
        return newCar;
    }

}
package br.uefs.utils;

import br.uefs.car.Car;
import br.uefs.car.CarReference;
import br.uefs.gas_station.GasStation;
import br.uefs.gas_station.GasStationReference;

public class Mapper {

    public static GasStationReference toGasStationReference(GasStation gasStation) {
        return GasStationReference.builder()
                .stationId(gasStation.getStationId())
                .coordinates(gasStation.getCoordinates())
                .rechargeTime(gasStation.getRechargeTime())
                .stationName(gasStation.getStationName())
                .carsInLine(gasStation.getCarsInLine())
                .build();
    }

    public static CarReference toCarReference(Car car) {
        return CarReference.builder()
                .idCar(car.getIdCar())
                .distanceForKMRateByPercentage(car.getDistanceForKMRateByPercentage())
                .timePerKmTraveled(car.getTimePerKmTraveled())
                .currentBatteryCharge(car.getBattery().getCurrentCharge())
                .coordinates(car.getCoordinates())
                .build();
    }

}

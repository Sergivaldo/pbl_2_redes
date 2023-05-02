package br.uefs.utils;

import br.uefs.car.Car;
import br.uefs.dto.LocalServerDTO;
import br.uefs.dto.CarDTO;
import br.uefs.gas_station.GasStation;
import br.uefs.dto.GasStationDTO;
import br.uefs.local_server.LocalServer;

public class Mapper {

    public static GasStationDTO toGasStationDTO(GasStation gasStation) {
        return GasStationDTO.builder()
                .stationId(gasStation.getStationId())
                .coordinates(gasStation.getCoordinates())
                .rechargeTime(gasStation.getRechargeTime())
                .stationName(gasStation.getStationName())
                .carsInQueue(gasStation.getCarsInQueue())
                .build();
    }

    public static CarDTO toCarDTO(Car car) {
        return CarDTO.builder()
                .idCar(car.getIdCar())
                .distanceForKMRateByPercentage(car.getDistanceByBatteryPercent())
                .timePerKmTraveled(car.getTimePerDistanceTraveled())
                .currentBatteryCharge(car.getBattery().getCurrentCharge())
                .coordinates(car.getCoordinates())
                .build();
    }

    public static LocalServerDTO toLocalServerDTO(LocalServer localServer){
        return LocalServerDTO
                .builder()
                .gasStations(localServer.getGasStations())
                .mqttUrl(localServer.getMqttClient().getServerURI())
                .name(localServer.getName())
                .build();
    }

}

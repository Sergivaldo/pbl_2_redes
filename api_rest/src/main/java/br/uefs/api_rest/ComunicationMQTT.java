package br.uefs.api_rest;

import org.springframework.context.ApplicationListener;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class ComunicationMQTT {
    private String REQUEST_RECHARGE_LOCAL = "/requestRecharge/local/";
    private String REQUEST_RECHARGE_EXTERNAL = "/requestRecharge/external/";
    private String RECEIVE_GAS_STATION_LOCAL = "/receiveGasStation/local/";
    private String RECEIVE_GAS_STATION_EXTERNAL = "/receiveGasStation/external/";

    public static Map<String, GasStation> listGasStations = new TreeMap<>();

    private void requestRecharge() throws InterruptedException {

        ClientMQTT clientMQTT = new ClientMQTT("tcp://127.0.0.1:1883", null, null);
        clientMQTT.startOn();
        Thread.sleep(1000);
        String message = "Preciso recarregar a bateria";
        new ReceiveMQTT(clientMQTT, RECEIVE_GAS_STATION_LOCAL,0);
        clientMQTT.publish(REQUEST_RECHARGE_LOCAL, message.getBytes(), 0);
        Thread.sleep(5000);

        // Tempo máximo aceitável =  50 min
        GasStation bestGasStation = SelectGasStation.selectBestGasStation(ApiRestApplication.car, listGasStations);
        if(bestGasStation.getWaitingTime() + (ApiRestApplication.car.getTimePerKmTraveled() * SelectGasStation.getDistance(ApiRestApplication.car.getLocationCoordinatesCar(), bestGasStation.getLocationCoordinatesGasStation())) <= 50){
            ApiRestApplication.bestGasStation = bestGasStation;
        }else{
            clientMQTT.unsubscribe(RECEIVE_GAS_STATION_LOCAL);
            new ReceiveMQTT(clientMQTT, RECEIVE_GAS_STATION_EXTERNAL,0);
            clientMQTT.publish(REQUEST_RECHARGE_EXTERNAL, message.getBytes(), 0);
            Thread.sleep(5000);
            GasStation bestGasStationExternal = SelectGasStation.selectBestGasStation(ApiRestApplication.car, listGasStations);
            ApiRestApplication.bestGasStation = bestGasStation;
        }
    }
}

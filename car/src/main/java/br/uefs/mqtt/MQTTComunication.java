package br.uefs.mqtt;

import br.uefs.car.CarApplication;
import br.uefs.car.SelectGasStation;
import br.uefs.gas_station.GasStation;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.TreeMap;

public class MQTTComunication {
    private String REQUEST_RECHARGE_LOCAL = "/requestRecharge/local/";
    private String REQUEST_RECHARGE_EXTERNAL = "/requestRecharge/external/";
    private String RECEIVE_GAS_STATION_LOCAL = "/receiveGasStation/local/";
    private String RECEIVE_GAS_STATION_EXTERNAL = "/receiveGasStation/external/";

    public static Map<String, GasStation> listGasStations = new TreeMap<>();

    private void requestRecharge() throws InterruptedException {

        MQTTClient MQTTClient = new MQTTClient("tcp://127.0.0.1:1883", null, null);
        MQTTClient.startOn();
        Thread.sleep(1000);
        String message = "Preciso recarregar a bateria";
        new ReceiveMQTT(MQTTClient, RECEIVE_GAS_STATION_LOCAL,0);
        MQTTClient.publish(REQUEST_RECHARGE_LOCAL, message.getBytes(), 0);
        Thread.sleep(5000);

        // Tempo máximo aceitável =  50 min
        GasStation bestGasStation = SelectGasStation.selectBestGasStation(CarApplication.car, listGasStations);
        if(bestGasStation.getWaitingTime() + (CarApplication.car.getTimePerKmTraveled() * SelectGasStation.getDistance(CarApplication.car.getCoordinates(), bestGasStation.getCoordinates())) <= 50){
            CarApplication.bestGasStation = bestGasStation;
        }else{
            MQTTClient.unsubscribe(RECEIVE_GAS_STATION_LOCAL);
            new ReceiveMQTT(MQTTClient, RECEIVE_GAS_STATION_EXTERNAL,0);
            MQTTClient.publish(REQUEST_RECHARGE_EXTERNAL, message.getBytes(), 0);
            Thread.sleep(5000);
            GasStation bestGasStationExternal = SelectGasStation.selectBestGasStation(CarApplication.car, listGasStations);
            CarApplication.bestGasStation = bestGasStation;
        }
    }
}

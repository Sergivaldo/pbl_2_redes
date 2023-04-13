package br.uefs.ServerLocal;

import br.uefs.gas_station.GasStation;
import br.uefs.gas_station.GasStationReference;
import br.uefs.mqtt.MQTTClient;

import java.util.Map;
import java.util.TreeMap;

public class LocalServerApplication {

    public static void main(String[] args) {
        Map<String, GasStationReference> gasStations = new TreeMap<>();
        MQTTClient MQTTClient = new MQTTClient("tcp://127.0.0.1:1883", null, null);
        LocalServer localServer = new LocalServer(MQTTClient, gasStations);
    }
}

package br.uefs.local_server;

import br.uefs.dto.GasStationDTO;
import br.uefs.mqtt.MQTTClient;

import java.util.Map;
import java.util.TreeMap;

public class LocalServerApplication {

    public static void main(String[] args) {
        Map<String, GasStationDTO> gasStations = new TreeMap<>();
        MQTTClient MQTTClient = new MQTTClient("tcp://127.0.0.1:1883", null, null);
        LocalServer localServer = new LocalServer(MQTTClient, gasStations);
    }
}

package br.uefs.local_server;

import br.uefs.dto.GasStationDTO;
import br.uefs.mqtt.MQTTClient;
import br.uefs.mqtt.MQTTClientParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LocalServerApplication {
    public static int port;
    public static void main(String[] args) {
        List<String> properties = Arrays.asList(args);
        Map<String, GasStationDTO> gasStations = new TreeMap<>();
        LocalServerParser.parsePort(properties);
        MQTTClient MQTTClient = MQTTClientParser.parseMQTTClient(properties);
        LocalServer localServer = new LocalServer(MQTTClient, gasStations);
        localServer.start();
        new ReceivedCentralServer(gasStations, port).start();
    }
}

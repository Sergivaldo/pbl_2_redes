package br.uefs.gas_station;

import br.uefs.mqtt.MQTTClient;
import br.uefs.mqtt.MQTTClientParser;

import java.util.Arrays;
import java.util.List;

public class GasStationApplication {
    public static void main(String[] args) {
        List<String> properties = Arrays.asList(args);
        MQTTClient mqttClient = MQTTClientParser.parseMQTTClient(properties);
        GasStation gasStation = GasStationParser.parseGasStation(properties);
        gasStation.setMqttClient(mqttClient);
        gasStation.start();
    }
}

package br.uefs.gas_station;

import br.uefs.mqtt.MQTTClient;

public class GasStationApplication {
    public static void main(String[] args) {
        MQTTClient mqttClient = new MQTTClient("tcp://127.0.0.1:1883", null, null);
        GasStation gasStation = new GasStation(
                new int[]{100,50},
                "posto1",
                "0001",
                5,
                10,
                mqttClient
        );

        gasStation.start();
    }
}

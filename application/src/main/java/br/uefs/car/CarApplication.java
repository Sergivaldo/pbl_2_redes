package br.uefs.car;

import br.uefs.mqtt.MQTTClientParser;

import java.util.Arrays;
import java.util.List;

public class CarApplication {

    public static void main(String[] args) {
        List<String> properties = Arrays.asList(args);
        Car car = CarParser.parseCar(properties);
        car.setMqttClient(MQTTClientParser.parseMQTTClient(properties));
        car.start();
    }
}

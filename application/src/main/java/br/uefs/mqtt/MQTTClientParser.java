package br.uefs.mqtt;

import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.Log;

import java.util.List;
import java.util.Objects;

public class MQTTClientParser {
    private static final String BASE_URL = "tcp://";

    public static MQTTClient parseMQTTClient(List<String> properties) {
        Objects.requireNonNull(properties);
        MQTTClient newMQTTClient = null;
        try {
            newMQTTClient = new MQTTClient.MQTTClientBuilder()
                    .serverURI(BASE_URL + parseHost(properties) + ":" + parsePort(properties))
                    .build();
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
        return newMQTTClient;
    }

    private static String parseHost(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-h");
        if (indexProperty != -1) {
            return properties.get(indexProperty + 1);
        } else {
            throw new NoSuchPropertyException("-h property not found");
        }
    }

    private static String parsePort(List<String> properties) throws NoSuchPropertyException {
        int indexProperty = properties.indexOf("-p");

        if (indexProperty != -1) {
            return properties.get(indexProperty + 1);
        } else {
            throw new NoSuchPropertyException("-p property not found");
        }
    }
}

package br.uefs.mqtt;

import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.Log;
import br.uefs.utils.PropertiesParser;

import java.util.List;
import java.util.Objects;

public class MQTTClientParser {
    private static final String BASE_URL = "tcp://";

    public static MQTTClient parseMQTTClient(List<String> properties) {
        Objects.requireNonNull(properties);
        MQTTClient newMQTTClient = null;
        PropertiesParser parser = new PropertiesParser(properties);
        try {

            newMQTTClient = new MQTTClient.MQTTClientBuilder()
                    .serverURI(BASE_URL + parser.parseString("-h") + ":" + parser.parseString("-p"))
                    .build();
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
        return newMQTTClient;
    }

}
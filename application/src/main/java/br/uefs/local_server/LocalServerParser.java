package br.uefs.local_server;

import br.uefs.dto.CentralServerDTO;
import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.mqtt.MQTTClientParser;
import br.uefs.utils.PropertiesParser;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class LocalServerParser {
    public static LocalServer parseLocalServer(List<String> properties) {
        Objects.requireNonNull(properties);
        PropertiesParser parser = new PropertiesParser(properties);
        LocalServer localServer = null;
        try {
            CentralServerDTO centralServerDTO = new CentralServerDTO(parser.parseInt("-gas_station_receiver_port"),
                    parser.parseInt("-solicitation_car_receiver_port"),parser.parseString("-central_server_host"));
            localServer = LocalServer.builder()
                    .gasStations(new TreeMap<>())
                    .mqttClient(MQTTClientParser.parseMQTTClient(properties))
                    .centralServer(centralServerDTO)
                    .build();
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
        return localServer;
    }
}

package br.uefs.central_server;

import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.PropertiesParser;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class CentralServerParser {
    public static CentralServer parseCentralServer(List<String> properties) {
        requireNonNull(properties);
        CentralServer centralServer = null;
        PropertiesParser parser = new PropertiesParser(properties);
        try {
            centralServer = CentralServer.builder()
                    .gasStationsReceiverPort(parser.parseInt("-gas_station_receiver_port"))
                    .solicitationCarReceiverPort(parser.parseInt("-solicitation_car_receiver_port"))
                    .host(parser.parseString("-central_server_host"))
                    .build();
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
        return centralServer;
    }
}

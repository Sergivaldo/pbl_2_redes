package br.uefs.central_server;

import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.PropertiesParser;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class CentralServerParser {
    public static void parseListPorts(List<String> properties) {
        requireNonNull(properties);
        PropertiesParser parser = new PropertiesParser(properties);
        try {
            CentralServerApplication.cloudPorts = parser.parseIntArray("-ls_ports");
            CentralServerApplication.host = parser.parseString("-h");
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
    }

    public static void parseHost(List<String> properties) {
        requireNonNull(properties);
        PropertiesParser parser = new PropertiesParser(properties);
        try {
            CentralServerApplication.host = parser.parseString("-h");
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
    }

    public static void parsePort(List<String> properties){
        requireNonNull(properties);
        PropertiesParser parser = new PropertiesParser(properties);
        try {
            CentralServerApplication.port = parser.parseInt("-p");
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
    }
}

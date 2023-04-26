package br.uefs.local_server;

import br.uefs.exceptions.NoSuchPropertyException;
import br.uefs.utils.PropertiesParser;

import java.util.List;
import java.util.Objects;

public class LocalServerParser {
    public static void parsePort(List<String> properties) {
        Objects.requireNonNull(properties);
        PropertiesParser parser = new PropertiesParser(properties);
        try {
             LocalServerApplication.port = parser.parseInt("-p");
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
        }
    }
}

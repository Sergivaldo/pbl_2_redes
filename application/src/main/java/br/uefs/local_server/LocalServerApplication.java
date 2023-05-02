package br.uefs.local_server;

import java.util.Arrays;
import java.util.List;

public class LocalServerApplication {
    public static int port;
    public static void main(String[] args) {
        List<String> properties = Arrays.asList(args);
        LocalServer localServer = LocalServerParser.parseLocalServer(properties);
        localServer.start();
    }
}

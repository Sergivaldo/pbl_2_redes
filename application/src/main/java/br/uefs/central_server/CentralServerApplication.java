package br.uefs.central_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CentralServerApplication {


    public static void main(String[] args) throws IOException {
        List<String> properties = Arrays.asList(args);
        CentralServer centralServer = CentralServerParser.parseCentralServer(properties);
        centralServer.start();
    }
}

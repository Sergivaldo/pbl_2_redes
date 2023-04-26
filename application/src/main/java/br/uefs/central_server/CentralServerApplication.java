package br.uefs.central_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CentralServerApplication {

    public static int[] cloudPorts;
    public static String host;
    public static int port;
    public static void main(String[] args) throws IOException {
        List<String> properties = Arrays.asList(args);
        CentralServerParser.parseListPorts(properties);
        CentralServerParser.parseHost(properties);
        CentralServerParser.parsePort(properties);
        ServerSocket socket = new ServerSocket(port);
        System.out.println("Abriu 9090");
        while (true) {
            Socket socketCentral = socket.accept();
            new CentralServer(socketCentral, host).start();
        }
    }
}

package br.uefs.central_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;

public class CentralServerApplication {

    public static int[] cloudPorts;

    public static void main(String[] args) throws IOException {
        CentralServerParser.parseListPorts(Arrays.asList(args));
        ServerSocket socket = new ServerSocket(9090);
        System.out.println("Abriu 9090");
        while (true) {
            Socket socketCentral = socket.accept();
            new CentralServer(socketCentral).start();
        }
    }
}

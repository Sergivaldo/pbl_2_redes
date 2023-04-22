package br.uefs.central_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class CentralServerApplication {

    public static LinkedList<Integer> cloudPorts;
    public static void main(String[] args) throws IOException {

        ServerSocket socket = new ServerSocket(9090);
        System.out.println("Abriu 9090");
        while (true) {
            Socket socketCentral = socket.accept();
            new CentralServer(socketCentral).start();
        }
    }
}

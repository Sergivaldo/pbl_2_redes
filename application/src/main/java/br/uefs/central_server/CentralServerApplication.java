package br.uefs.central_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralServerApplication {
    public static int port = 3000;
    public static void main(String[] args) throws IOException {

        ServerSocket socket = new ServerSocket(9090);
        System.out.println("Abriu 9090");
        while (true) {
            Socket socketCentral = socket.accept();
            new CentralServer(socketCentral).start();
        }
    }
}

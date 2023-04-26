package br.uefs.local_server;

import br.uefs.dto.GasStationDTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ReceivedCentralServer extends Thread{
    private static Map<String, GasStationDTO> gasStations;

    public ReceivedCentralServer(Map<String, GasStationDTO> gasStations){
        this.gasStations = gasStations;
    }
    public void run(){
        ServerSocket socket = null;
        try {
            socket = new ServerSocket();
            while (true) {
                Socket socketCentral = socket.accept();
                new ReceivedRequisition(socketCentral,gasStations).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
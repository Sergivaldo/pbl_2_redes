package br.uefs.central_server;

import br.uefs.dto.CentralServerDTO;
import br.uefs.dto.LocalServerDTO;
import br.uefs.utils.Log;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CentralServer {
    private final String host;
    private final int solicitationCarReceiverPort;
    private final int gasStationsReceiverPort;
    private final List<LocalServerDTO> localServers;

    @Builder
    public CentralServer(String host, int gasStationsReceiverPort, int solicitationCarReceiverPort) {
        this.host = host;
        this.gasStationsReceiverPort = gasStationsReceiverPort;
        this.solicitationCarReceiverPort = solicitationCarReceiverPort;
        localServers = new ArrayList<>();
    }

    public void start() throws IOException {
        new SolicitationCarReceiver(solicitationCarReceiverPort, host).start();
        new GasStationsReceiver().start();
    }

    public class GasStationsReceiver extends Thread {
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(gasStationsReceiverPort);
                Log.success("Socket conectado na porta "+gasStationsReceiverPort);
                while (true) {
                    Socket fogSocket = serverSocket.accept();
                    new GasStationReceiverProcessor(localServers, fogSocket).start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @AllArgsConstructor
    public class SolicitationCarReceiver extends Thread {
        private int port;
        private String host;

        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(solicitationCarReceiverPort);
                Log.success("Socket conectado na porta "+ solicitationCarReceiverPort);
                while (true) {
                    Socket fogSocket = serverSocket.accept();
                    new SolicitationCarReceiverProcessor(fogSocket, new CentralServerDTO(port, host,localServers)).start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

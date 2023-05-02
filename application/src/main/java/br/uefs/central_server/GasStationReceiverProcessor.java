package br.uefs.central_server;

import br.uefs.dto.LocalServerDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class GasStationReceiverProcessor extends Thread{

    private List<LocalServerDTO> localServers;
    private Socket fogSocket;

    public GasStationReceiverProcessor( List<LocalServerDTO> localServers,Socket fogSocket){
        this.localServers = localServers;
        this.fogSocket = fogSocket;
    }

    public void run(){
        try {
            ObjectInputStream in = new ObjectInputStream(fogSocket.getInputStream());
            LocalServerDTO localServerDTO = (LocalServerDTO) in.readObject();
            System.out.println(localServerDTO.getName());
            if(!localServers.isEmpty()){
                localServers.removeIf(l -> l.getName().equals(localServerDTO.getName()));
            }
            localServers.add(localServerDTO);
            fogSocket.close();
            System.out.println(localServerDTO.getName());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}

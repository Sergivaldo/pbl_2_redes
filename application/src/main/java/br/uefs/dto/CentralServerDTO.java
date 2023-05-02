package br.uefs.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CentralServerDTO {
    private  int solicitationCarReceiverPort;
    private int gasStationsReceiverPort;
    private String host;
    private List<LocalServerDTO> localServers;

    public CentralServerDTO(int gasStationsReceiverPort,int solicitationCarReceiverPort, String host) {
        this.solicitationCarReceiverPort = solicitationCarReceiverPort;
        this.host = host;
        this.gasStationsReceiverPort = gasStationsReceiverPort;
    }

    public CentralServerDTO(int solicitationCarReceiverPort, String host, List<LocalServerDTO> localServers) {
        this.solicitationCarReceiverPort = solicitationCarReceiverPort;
        this.host = host;
        this.localServers = localServers;
    }
}

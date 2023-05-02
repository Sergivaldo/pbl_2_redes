package br.uefs.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CentralServerDTO {
    private  int port;
    private String host;
    private List<LocalServerDTO> localServers;

    public CentralServerDTO(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public CentralServerDTO(int port, String host, List<LocalServerDTO> localServers) {
        this.port = port;
        this.host = host;
        this.localServers = localServers;
    }
}

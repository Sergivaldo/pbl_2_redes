package br.uefs.dto;

import br.uefs.dto.GasStationDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class LocalServerDTO {
    private String name;
    private String mqttUrl;
    private Map<String, GasStationDTO> gasStations;

    @Builder
    public LocalServerDTO(String name, String mqttUrl, Map<String, GasStationDTO> gasStations) {
        this.name = name;
        this.mqttUrl = mqttUrl;
        this.gasStations = gasStations;
    }
}

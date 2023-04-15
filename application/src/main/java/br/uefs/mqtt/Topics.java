package br.uefs.mqtt;

import lombok.Getter;

@Getter
public enum Topics {
    CAR_REQUEST_RECHARGE("/Car/requestRecharge/"),                    // tópico em que o carro faz solicitação de recarga
    CAR_RECEIVE_GAS_STATION ("/Server/publishGasStation/"),           // tópico em que o carro recebe o melhor posto
    GAS_STATION_RECEIVE_REQUEST ("/Server/requestStatusGasStation/"), // tópico em que o posto recebe o pedido do seu status
    GAS_STATION_PUBLISH_STATUS ("/GasStation/publishStatus/");        // tópico em que o posto pubica as suas informaçãoes

   private String value;
    Topics(String value) {
        this.value = value;
    }
}

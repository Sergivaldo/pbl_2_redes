package br.uefs.api_rest;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class ComunicationMQTT {
    private String REQUEST_RECHARGE_LOCAL = "/requestRecharge/local/";
    private String REQUEST_RECHARGE_EXTERNAL = "/requestRecharge/external/";
    private String RECEIVE_GAS_STATION_LOCAL = "/receiveGasStation/local/";
    private String RECEIVE_GAS_STATION_EXTERNAL = "/receiveGasStation/external/";

    private Map<String, GasStation> listGasStations = new TreeMap<>();

    private void requestRecharge() throws InterruptedException {


        ClientMQTT clientMQTT = new ClientMQTT("tcp://127.0.0.1:1883", null, null);
        clientMQTT.startOn();
        Thread.sleep(1000);
        String message = "Preciso recarregar a bateria";
        new ReceiveMQTT(clientMQTT, RECEIVE_GAS_STATION_LOCAL,0);
        clientMQTT.publish(REQUEST_RECHARGE_LOCAL, message.getBytes(), 0);
        Thread.sleep(5000);

    }
}

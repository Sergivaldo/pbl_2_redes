package br.uefs.api_rest;

public class ComunicationMQTT {
    private String REQUEST_RECHARGE_LOCAL = "/requestRecharge/local/";
    private String REQUEST_RECHARGE_EXTERNAL = "/requestRecharge/external/";
    private String RECEIVE_GAS_STATION_LOCAL = "/receiveGasStation/local/";
    private String RECEIVE_GAS_STATION_EXTERNAL = "/receiveGasStation/external/";
    private void requestRecharge() throws InterruptedException {
        ClientMQTT clientMQTT = new ClientMQTT("tcp://127.0.0.1:1883", null, null);
        clientMQTT.startOn();
        Thread.sleep(1000);
        String message = "Preciso recarregar a bateria";
        //clientMQTT.subscribe(0, ,RECEIVE_GAS_STATION_LOCAL, RECEIVE_GAS_STATION_EXTERNAL);
        clientMQTT.publish(REQUEST_RECHARGE_LOCAL, message.getBytes(), 0);

    }
}

package br.uefs;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ClientMQTT clientMQTT = new ClientMQTT("EX: tcp://broker.mqttdashboard.com:1883", null, null);
        clientMQTT.start();
        Thread.sleep(1000);
        String message = "Teste de MQTT ";

        clientMQTT.publish("teste", message.getBytes(), 0);
    }
}
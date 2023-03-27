package br.uefs;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ClientMQTT clientMQTT = new ClientMQTT("tcp://127.0.0.1:1883", null, null);
        clientMQTT.start();
        Thread.sleep(1000);
        String message = "Teste de MQTT ";

        clientMQTT.publish("test", message.getBytes(), 0);
    }
}
package br.uefs;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ClientMQTT clientMQTT = new ClientMQTT("EX: tcp://broker.mqttdashboard.com:1883", null, null);
        clientMQTT.start();
        Thread.sleep(1000);
        String mensagem = "Teste de MQTT ";

        clientMQTT.publish("teste", mensagem.getBytes(), 0);
    }
}
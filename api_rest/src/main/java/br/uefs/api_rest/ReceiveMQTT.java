package br.uefs.api_rest;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ReceiveMQTT implements IMqttMessageListener {
    public ReceiveMQTT(ClientMQTT clientMQTT, String topic, int qos) {
        clientMQTT.subscribe(qos, this, topic);
    }

    @Override
    public void messageArrived(String topico, MqttMessage mm) throws Exception {
        System.out.println("Mensagem recebida:");
        System.out.println("\tTópico: " + topico);
        System.out.println("\tMensagem: " + new String(mm.getPayload()));
        System.out.println("");
    }
}
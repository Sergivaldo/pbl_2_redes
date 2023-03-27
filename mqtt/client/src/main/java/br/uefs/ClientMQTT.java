package br.uefs;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.Arrays;

public class ClientMQTT implements MqttCallbackExtended {

    private final String serverURI;
    private MqttClient client;
    private final MqttConnectOptions mqttOptions;

    public ClientMQTT(String serverURI, String user, String password) {
        this.serverURI = serverURI;

        mqttOptions = new MqttConnectOptions();
        mqttOptions.setMaxInflight(200);
        mqttOptions.setConnectionTimeout(3);
        mqttOptions.setKeepAliveInterval(10);
        mqttOptions.setAutomaticReconnect(true);
        mqttOptions.setCleanSession(false);

        if (user != null && password != null) {
            mqttOptions.setUserName(user);
            mqttOptions.setPassword(password.toCharArray());
        }
    }

    /**
     * Se increver em tópicos
     *
     * @param qos
     * @param gestorMensagemMQTT
     * @param topics
     * @return
     */
    public IMqttToken subscribe(int qos, IMqttMessageListener gestorMensagemMQTT, String... topics) {
        if (client == null || topics.length == 0) {
            return null;
        }
        int size = topics.length;
        int[] qoss = new int[size];
        IMqttMessageListener[] listners = new IMqttMessageListener[size];

        for (int i = 0; i < size; i++) {
            qoss[i] = qos;
            listners[i] = gestorMensagemMQTT;
        }
        try {
            return client.subscribeWithResponse(topics, qoss, listners);
        } catch (MqttException ex) {
            System.out.println(String.format("Erro ao se inscrever nos tópicos %s - %s", Arrays.asList(topics), ex));
            return null;
        }
    }

    /**
     *
     * @param topics
     */
    public void unsubscribe(String... topics) {
        if (client == null || !client.isConnected() || topics.length == 0) {
            return;
        }
        try {
            client.unsubscribe(topics);
        } catch (MqttException ex) {
            System.out.println(String.format("Erro ao se desinscrever no tópico %s - %s", Arrays.asList(topics), ex));
        }
    }

    public void start() {
        try {
            System.out.println("Conectando no broker MQTT em " + serverURI);
            client = new MqttClient(serverURI, String.format("cliente_java_%d", System.currentTimeMillis()), new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
            client.setCallback(this);
            client.connect(mqttOptions);
        } catch (MqttException ex) {
            System.out.println("Erro ao se conectar ao broker mqtt " + serverURI + " - " + ex);
        }
    }

    public void finish() {
        if (client == null || !client.isConnected()) {
            return;
        }
        try {
            client.disconnect();
            client.close();
        } catch (MqttException ex) {
            System.out.println("Erro ao desconectar do broker mqtt - " + ex);
        }
    }

    public void publish(String topic, byte[] payload, int qos) {
        publish(topic, payload, qos, false);
    }

    public synchronized void publish(String topic, byte[] payload, int qos, boolean retained) {
        try {
            if (client.isConnected()) {
                client.publish(topic, payload, qos, retained);
                System.out.println(String.format("Tópico %s publicado. %dB", topic, payload.length));
            } else {
                System.out.println("Cliente desconectado, não foi possível publicar o tópico " + topic);
            }
        } catch (MqttException ex) {
            System.out.println("Erro ao publicar " + topic + " - " + ex);
        }
    }

    @Override
    public void connectComplete(boolean b, String s) {

    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}

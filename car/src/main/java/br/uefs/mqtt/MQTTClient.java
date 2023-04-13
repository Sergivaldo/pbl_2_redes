package br.uefs.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.Arrays;

public class MQTTClient {

    private final String serverURI;
    private MqttClient client;
    private final MqttConnectOptions mqttOptions;

    private final int subscribeQos = 0;

    public MQTTClient(String serverURI, String user, String password) {
        this.serverURI = serverURI;
        mqttOptions = new MqttConnectOptions();

        if (user != null && password != null) {
            mqttOptions.setUserName(user);
            mqttOptions.setPassword(password.toCharArray());
        }

        defaultMqttOptions();
    }

    private void defaultMqttOptions(){
        mqttOptions.setMaxInflight(200);
        mqttOptions.setConnectionTimeout(3);
        mqttOptions.setKeepAliveInterval(10);
        mqttOptions.setAutomaticReconnect(true);
        mqttOptions.setCleanSession(false);
    }

    /**
     *
     * @param topic
     * @param listener
     * @return
     */
    public IMqttToken subscribe(String topic, IMqttMessageListener listener) {
        if (client == null || topic.length() == 0) {
            return null;
        }
        try {
            return client.subscribeWithResponse(topic, subscribeQos, listener);
        } catch (MqttException ex) {
            System.out.println(String.format("Erro ao se inscrever no tópico %s - %s", topic, ex));
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

    public void startOn() {
        try {
            System.out.println("Conectando no broker MQTT em " + serverURI);
            client = new MqttClient(serverURI,
                    String.format("%d", System.currentTimeMillis()),
                    new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
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

    public void publish(String topic, byte[] payload, int qos, boolean retained) {
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
}

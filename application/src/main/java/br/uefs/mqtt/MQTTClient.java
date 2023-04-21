package br.uefs.mqtt;

import br.uefs.utils.Log;
import lombok.Builder;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;

public class MQTTClient{
    private final String serverURI;
    private MqttAsyncClient client;
    private final MqttConnectOptions mqttOptions;
    private final int subscribeQos = 2;
    private final int publishQos = 0;

    @Builder
    public MQTTClient(String serverURI) {
        this.serverURI = serverURI;
        mqttOptions = new MqttConnectOptions();
        defaultMqttOptions();
    }

    public MQTTClient(String serverURI, String user, String password) {
        this.serverURI = serverURI;
        mqttOptions = new MqttConnectOptions();

        if (user != null && password != null) {
            mqttOptions.setUserName(user);
            mqttOptions.setPassword(password.toCharArray());
        }

        defaultMqttOptions();
    }

    private void defaultMqttOptions() {
        mqttOptions.setMaxInflight(200);
        mqttOptions.setConnectionTimeout(3);
        mqttOptions.setKeepAliveInterval(1000000);
        mqttOptions.setAutomaticReconnect(true);
        mqttOptions.setCleanSession(true);
    }

    /**
     * @param topic
     * @param listener
     * @return
     */
    public IMqttToken subscribe(String topic, IMqttMessageListener listener) {
        if (client == null || topic.length() == 0) {
            return null;
        }

        try {
            IMqttToken token = client.subscribe(topic, subscribeQos, listener);
            Log.success("Inscrito no tópico: "+ topic);
            return token;
        } catch (MqttException ex) {
            Log.error("Erro ao se inscrever no tópico " + topic);
            return null;
        }
    }

    /**
     * @param topics
     */
    public void unsubscribe(String... topics) {
        if (client == null || !client.isConnected() || topics.length == 0) {
            return;
        }
        try {
            client.unsubscribe(topics);
        } catch (MqttException ex) {
            Log.error("Erro ao se desinscrever no tópico");
        }
    }

    public void startOn() {
        try {
            client = new MqttAsyncClient(serverURI,
                    String.format("%d", System.currentTimeMillis()),
                    new MemoryPersistence());
            client.connect(mqttOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.success("Conectado ao broker: " + serverURI);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.error("Erro ao se conectar ao broker mqtt " + serverURI);
                }
            }).waitForCompletion();
        } catch (MqttException ex) {
            ex.printStackTrace();
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
            Log.error("Erro ao desconectar do broker mqtt - " + ex);
        }
    }

    public void publish(String topic, byte[] payload) {

        publish(topic, payload, publishQos, false);
    }

    public boolean connected() {
        return client.isConnected();
    }

    public void publish(String topic, byte[] payload, int qos, boolean retained) {
        try {
            if (client.isConnected()) {
                client.publish(topic, payload, qos, retained);
            } else {
                Log.error("Cliente desconectado, não foi possível publicar o tópico " + topic);
            }
        } catch (MqttException ex) {
            Log.error("Erro ao publicar " + topic + " - " + ex);
        }
    }


}

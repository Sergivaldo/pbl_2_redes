package br.uefs.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Listener implements IMqttMessageListener {
    private ExecutorService service = Executors.newFixedThreadPool(5);

    private Consumer<MqttMessage> task;

    public Listener(Consumer<MqttMessage> task) {
        this.task = task;
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        service.submit(()->{task.accept(mqttMessage);});
    }



}

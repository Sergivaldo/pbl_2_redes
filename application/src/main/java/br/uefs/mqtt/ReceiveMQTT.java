package br.uefs.mqtt;

import br.uefs.gas_station.GasStation;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ReceiveMQTT implements IMqttMessageListener {
    public ReceiveMQTT(MQTTClient MQTTClient, String topic, int qos) {
        MQTTClient.subscribe(topic, this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        System.out.println("Novo posto adicionado");
        GasStation gasStation = new Gson().fromJson(new String(mm.getPayload()), GasStation.class);
        MQTTComunication.listGasStations.put(gasStation.getName(), gasStation);
    }
}
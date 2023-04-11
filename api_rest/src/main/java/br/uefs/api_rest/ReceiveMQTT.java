package br.uefs.api_rest;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ReceiveMQTT implements IMqttMessageListener {
    public ReceiveMQTT(ClientMQTT clientMQTT, String topic, int qos) {
        clientMQTT.subscribe(qos, this, topic);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        // Formato da menssagem: localizaçãox/localizaçãoy/nome_do_posto/idGasStation/carros_na_fila/tempo_de_recarga_por_carro
        System.out.println("Novo posto adicionado");
        String message = new String(mm.getPayload());
        System.out.println(message);
        String[] parameters = message.split("/");

        if (ComunicationMQTT.listGasStations.get(parameters[3]) == null){
            int[] locationCoordinates = new int[2];
            locationCoordinates[0] = Integer.parseInt(parameters[0]);
            locationCoordinates[1] = Integer.parseInt(parameters[1]);
            GasStation gasStation = new GasStation(locationCoordinates, parameters[2], parameters[3], Integer.parseInt(parameters[4]),Integer.parseInt(parameters[5]));
            ComunicationMQTT.listGasStations.put(parameters[3], gasStation);
        }
    }
}
package com.example.cerebro;

import android.util.Log;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.*;

public class MQTTclient implements MqttCallback {

    MqttClient myClient;
    private String message_string = "";
    private ChangeListener listener;
    private String IPport = "tcp://10.0.2.2:1883";      //for the emulator

    public void runClient() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            myClient = new MqttClient(IPport, "Cerebro2", persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);

            myClient.setCallback( this);
            myClient.connect(connOpts);

            myClient.subscribe("commands");
            Log.i("Mqtt Client","Subscribing to topic: commands");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public String getMessage_string(){
        return message_string;
    }

    public void setMessage_string(String message_string) {
        this.message_string = message_string;
        if (listener != null) listener.onChange();
    }

    public void sendMessage(String msg) {
        int qos = 2;
        MqttMessage message;
        message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        try {
            Log.i("Mqtt Client","Publishing to topic: frequency");
            myClient.publish("frequency", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.i("ERROR!!!", cause.getCause().toString());
        Log.i("Mqtt Client", "Connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        Log.i("Mqtt Client","------------------------------------------------");
        Log.i("Topic", topic);

        setMessage_string(new String(message.getPayload()));
        Log.i("Message", getMessage_string());
        Log.i("Mqtt Client","------------------------------------------------");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public void disconnect() {
        if (this.myClient != null) {
            try {
                this.myClient.disconnect();
                this.myClient.close();
            } catch (MqttException e) {
                System.out.println("Error disconnecting!");
            }
        }
    }

    public void setIPport(String input) {
        IPport = input;
    }
}

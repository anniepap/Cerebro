package com.example.cerebro;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.*;

public class MQTTclient implements MqttCallback {

    MqttClient myClient;
    private String message_string="";
    private ChangeListener listener;

    public MQTTclient() {
    }

    public static void main(String[] args) {
        new MQTTclient().runClient();
    }

    public void runClient() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            //tcp://10.0.2.2:1883 is the broker for the emulator
            myClient = new MqttClient("tcp://10.0.2.2:1883", "Cerebro2",persistence);
            Log.i("main","connected");
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            myClient.setCallback( this);
            myClient.connect(connOpts);

            myClient.subscribe("instructions");;
            Log.i("main","subscribed");

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

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost!");

    }

    @Override
    public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        Log.i("Main activity","------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Topic: " + topic);

        setMessage_string(new String(message.getPayload()));
        System.out.println("Main activity: " +getMessage_string());
        Log.i("Main activity","Message: " +getMessage_string());
        Log.i("Main activity","------------------------------------------------");
        System.out.println("-------------------------------------------------");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub

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

}

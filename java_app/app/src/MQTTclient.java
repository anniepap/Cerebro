import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.*;

public class MQTTclient implements MqttCallback {

    MqttClient myClient;
    int frequency = 2;

    @Override
    public void connectionLost(Throwable t) {
        System.out.println("Connection lost!");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("-------------------------------------------------");
        System.out.println("Topic: " + topic);
        System.out.println("Message arrived: " + new String(message.getPayload()));
        System.out.println("-------------------------------------------------");
        frequency = Integer.parseInt(new String(message.getPayload()));
    }

    public MQTTclient() {
        String clientId = "Cerebro1";
        String broker = "tcp://localhost:1883";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            myClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            myClient.setCallback(this);
            myClient.connect(connOpts);
        } catch (MqttException me) {
            me.printStackTrace();
        }

        runSubscribe();
    }

    public void runSubscribe() {
        int qos = 2;
        String topic = "frequency";
        try {
            System.out.println("Subscribing to topic: " + topic);
            myClient.subscribe(topic, qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runPublish(String command) {
        int qos = 2;
        String topic = "commands";

        if (!command.equals("finish")) {
            System.out.println("-------------------------------------------------");
            System.out.println("Publishing on topic: " + topic);
            System.out.println("Message: " + command);
            System.out.println("-------------------------------------------------");
            
            MqttMessage message = new MqttMessage(command.getBytes());
            message.setQos(qos);
            try {
                myClient.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            disconnect();
        }
    }

    public void disconnect() {
        try {
            // wait to ensure subscribed messages are delivered
            System.out.println("...");
            Thread.sleep(5000);
            myClient.disconnect();
            myClient.close();
            System.out.println("Finished!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error disconnecting!");
        }
    }

    public int getFrequency() {
        return frequency;
    }
}

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MQTTclient implements MqttCallback{

    MqttClient myClient;
    // MqttConnectOptions connOpt;

    static final Boolean subscriber = true;
    static final Boolean publisher = true;

    @Override
    public void connectionLost(Throwable t) {
        System.out.println("Connection lost!");
        // code to reconnect to the broker would go here if desired
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("-------------------------------------------------");
        System.out.println("| Topic:" + topic);
        //System.out.println("| Message: " + new String(message.getPayload()));
        System.out.println("-------------------------------------------------");
    }

    public static void main(String[] args) {
        MQTTclient cl = new MQTTclient();
        cl.runClient();
    }

    public void runClient()  {
        String clientId = "Cerebro1";
        String broker = "tcp://localhost:1883";
        MemoryPersistence persistence = new MemoryPersistence();



        try {
            myClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            myClient.setCallback(this);
            myClient.connect(connOpts);
        }
        catch (MqttException me){
            me.printStackTrace();
        }

        String topic = "instructions";

        if (publisher){
            String content = "turn On";
            String content1= "turn Off";
            String content2= "quit";
            int qos = 2;

            System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);

            //Scanner scanner = new Scanner(System.in);
            //String line = scanner.nextLine();
            //MqttMessage message = new MqttMessage(line.getBytes());

            try {
                myClient.publish(topic, message);
                System.out.println("Message published" +topic);
                Thread.sleep(30000);
                message = new MqttMessage(content1.getBytes());
                myClient.publish(topic, message);

            } catch (Exception me) {
                System.out.println("msg " + me.getMessage());
                System.out.println("loc " + me.getLocalizedMessage());
                System.out.println("cause " + me.getCause());
                System.out.println("excep " + me);
                me.printStackTrace();
            }
        }
        /*if (subscriber) {
            try {
                int qos = 2;

                System.out.println("Subscribing to topic \""+topic +"\"qos"+qos);
                myClient.subscribe(topic, qos);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        /*try {
            // wait to ensure subscribed messages are delivered
            if (subscriber) {
                Thread.sleep(5000);
            }
            myClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}

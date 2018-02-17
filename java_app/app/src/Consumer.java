import java.util.LinkedList;

public class Consumer implements Runnable {

    MQTTclient cl;
    LinkedList<String> buffer = new LinkedList<>();
    int capacity = 100;
    long previous = System.currentTimeMillis();

    Consumer(MQTTclient cl) {
        this.cl = cl;
        new Thread(this, "Consumer").start();
    }

    @Override
    public void run() {
        try {
            consume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (this) {
                long now = System.currentTimeMillis();
                if ((now - previous)/1000 >= cl.getFrequency()) {
                    previous = now;

                    // consumer thread waits if list is empty
                    while (buffer.size() == 0)
                        wait();

                    // get first item from the list and publish it
                    String command = buffer.removeFirst();
                    System.out.println("Consumer consumed: " + command);
                    cl.runPublish(command);

                    // Wake up producer thread
                    notify();
                    Thread.sleep(1000);

                    if (command.equals("finish"))
                        return;
                }
            }
        }
    }

    public LinkedList<String> getBuffer() {
        return buffer;
    }

}

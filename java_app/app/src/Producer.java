import java.util.LinkedList;

public class Producer implements Runnable {

    int capacity = 100;

    Producer() {
        new Thread(this, "Producer").start();
    }

    @Override
    public void run() {
    }

    public void produce(LinkedList<String> list, String command) throws InterruptedException {
        synchronized (this) {
            // producer thread waits if list is full
            //while (list.size() == capacity)
                //wait();

            // insert in list
            list.add(command);
            System.out.println("Producer produced: " + command);

            // notifies the consumer thread that now it can start consuming
            notify();
            Thread.sleep(1000);
        }
    }

}

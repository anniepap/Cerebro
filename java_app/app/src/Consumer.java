import java.util.LinkedList;

public class Consumer implements Runnable {

    //MQTTclient cl;
    LinkedList<String> list = new LinkedList<>();
    int capacity = 100;
    int frequency = 2;
    long previous = System.currentTimeMillis();

    Consumer() {
        //this.cl = cl;
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
                if ((now - previous)/1000 >= frequency) {
                    previous = now;

                    // consumer thread waits if list is empty
                    while (list.size() == 0)
                        wait();

                    // get first item from the list
                    String command = list.removeFirst();
                    System.out.println("Consumer consumed: " + command);
                    //////////////////////////////////////////////////////////////send mqtt

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
        return list;
    }

}

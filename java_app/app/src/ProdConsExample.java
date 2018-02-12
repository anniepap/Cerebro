import java.util.LinkedList;

public class ProdConsExample {
    public static void main(String[] args) throws InterruptedException {

        final PC pc = new PC();

        // Create producer thread
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.produce();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Create consumer thread
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.consume();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start both threads
        t1.start();
        t2.start();

        // Producer tread finishes before consumer
        t1.join();
        t2.join();
    }

    // Containts both producer and consumer
    public static class PC {
        // Use a list as buffer
        LinkedList<Integer> list = new LinkedList<>();
        int capacity = 3;

        // Function called by producer thread
        public void produce() throws InterruptedException {
            int value = 0;
            while (true) {
                synchronized (this) {
                    // producer thread waits while list is full
                    while (list.size() == capacity)
                        wait();

                    // insert in list
                    list.add(++value);
                    System.out.println("Producer produced: " + value);

                    // notifies the consumer thread that now it can start consuming
                    notify();
                    Thread.sleep(1000);

                    if (value == 5)
                        return;
                }
            }
        }

        // Function called by consumer thread
        public void consume() throws InterruptedException {
            while (true) {
                synchronized (this) {
                    // consumer thread waits while list is empty
                    while (list.size() == 0)
                        wait();

                    // get first item from the list
                    int value = list.removeFirst();
                    System.out.println("Consumer consumed: " + value);

                    // Wake up producer thread
                    notify();
                    Thread.sleep(1000);

                    if (value == 5)
                        return;
                }
            }
        }
    }
}
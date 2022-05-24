package queue;

import queue.LinkedQueue;

public class LinkedQueueTest {
    public static void fill(LinkedQueue queue, int length) {
        System.out.println("====FILL====");
        for (int i = 0; i < length; i++) {
            queue.enqueue("Current size: " + queue.size());
        }
        System.out.println("Fill complete! Current size: " + queue.size());
        System.out.println("------------");
    }

    public static void dump(LinkedQueue queue) {
        System.out.println("====DUMP====");
        while (!queue.isEmpty()) {
            System.out.println("Current element: " + queue.dequeue());
        }
        System.out.println("Dump complete! Current size: " + queue.size());
        System.out.println("------------");
    }

    public static void main(String[] args) {
        LinkedQueue test = new LinkedQueue();
        fill(test, 10);
        dump(test);
        fill(test, 5);
        test.clear();
        dump(test);
    }
}

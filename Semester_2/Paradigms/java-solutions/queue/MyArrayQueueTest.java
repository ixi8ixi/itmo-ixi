package queue;

public class MyArrayQueueTest {
    public static void fill(ArrayQueue queue, int length) {
        System.out.println("---- FILL ----");
        for (int i = 0; i < length; i++) {
            queue.enqueue("Current size: " + queue.size());
        }
        System.out.println("Fill complete! Current size: " + queue.size() + "\n");
    }

    public static void dump(ArrayQueue queue) {
        System.out.println("---- DUMP ----");
        while (!queue.isEmpty()) {
            System.out.println("Current element: " + queue.dequeue());
        }
        System.out.println("Dump complete! Current size: " + queue.size() + "\n");
    }

    public static void main(String[] args) {
        System.out.println("==== ARRAY QUEUE TEST ====");
        ArrayQueue queue = new ArrayQueue();
        fill(queue, 10);
        dump(queue);
        fill(queue, 5);
        queue.clear();
        dump(queue);
    }
}

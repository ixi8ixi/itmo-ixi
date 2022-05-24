package queue;

public class ArrayQueueADTTest {
    public static void fill(ArrayQueueADT queue, int length) {
        System.out.println("---- FILL ----");
        for (int i = 0; i < length; i++) {
            ArrayQueueADT.enqueue(queue, "Current size: " + ArrayQueueADT.size(queue));
        }
        System.out.println("Fill complete! Current size: " + ArrayQueueADT.size(queue) + "\n");
    }

    public static void dump(ArrayQueueADT queue) {
        System.out.println("---- DUMP ----");
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println("Current element: " + ArrayQueueADT.dequeue(queue));
        }
        System.out.println("Dump complete! Current size: " + ArrayQueueADT.size(queue) + "\n");
    }

    public static void main(String[] args) {
        System.out.println("==== ARRAY QUEUE ADT TEST ====");
        ArrayQueueADT queue = new ArrayQueueADT();
        fill(queue, 10);
        dump(queue);
        fill(queue, 5);
        ArrayQueueADT.clear(queue);
        dump(queue);
    }
}

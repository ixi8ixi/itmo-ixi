package queue;

public class ArrayQueueModuleTest {
    public static void fill(int length) {
        System.out.println("---- FILL ----");
        for (int i = 0; i < length; i++) {
            ArrayQueueModule.enqueue("Current size: " + ArrayQueueModule.size());
        }
        System.out.println("Fill complete! Current size: " + ArrayQueueModule.size() + "\n");
    }

    public static void dump() {
        System.out.println("---- DUMP ----");
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println("Current element: " + ArrayQueueModule.dequeue());
        }
        System.out.println("Dump complete! Current size: " + ArrayQueueModule.size() + "\n");
    }

    public static void main(String[] args) {
        System.out.println("==== ARRAY QUEUE MODULE TEST ====");
        fill(10);
        dump();
        fill(5);
        ArrayQueueModule.clear();
        dump();
    }
}

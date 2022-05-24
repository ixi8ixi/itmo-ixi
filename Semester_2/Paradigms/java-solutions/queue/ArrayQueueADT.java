package queue;

import java.util.Objects;

/*
    Model: a[1]..a[n]
    I: n >= 0 && for i = 1 .. n: a[i] != null

    immutable(n): for i = 1 .. n: a'[i] == a[i]
    shifted(n): for i = 2 .. n: a'[i] = a[i - 1]
    backshifted(n): for i
*/

public class ArrayQueueADT {
    private Object[] elements = new Object[16];
    private int head;
    private int size;

    // Pre: element != null && queue != null
    // Post: n' = n + 1 && a[1] == element && immutable(n)
    public static void enqueue(final ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(element);
        assert queue != null;
        ensureCapacity(queue);
        queue.size++;
        queue.elements[(queue.head + queue.size - 1) % queue.elements.length] = element;
    }

    // Pre: n >= 1 && queue != null
    // Post: R == a[n] && immutable(n) && n' == n
    public static Object element(final ArrayQueueADT queue) {
        assert queue != null;
        assert size(queue) >= 1;
//        System.out.println(Arrays.toString(elements));
        return queue.elements[queue.head];
    }

    // Pre: n >= 1 && queue != null
    // Post: R == a[n] && n' = n - 1 && immutable(n')
    public static Object dequeue(final ArrayQueueADT queue) {
        assert queue != null;
        assert size(queue) >= 1;
        final Object result = queue.elements[queue.head];
        queue.size--;
        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.elements.length;
        return result;
    }

    // Pred: queue != null
    // Post: R == n && n' == n && immutable(n)
    public static int size(final ArrayQueueADT queue) {
        assert queue != null;
        return queue.size;
    }

    // Pred: queue != null
    // Post: R == (n == 0) && n' == n && immutable(n)
    public static boolean isEmpty(final ArrayQueueADT queue) {
        assert queue != null;
        return queue.size == 0;
    }

    // Pred: queue != null
    // Post: n' = 0
    public static void clear(final ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        final int currentSize = size(queue);
        for (int i = 0; i < currentSize; i++) {
            dequeue(queue);
        }
    }

    // Pred: element != null && queue != null
    // Post: n' = n + 1 && a[1] == element && shifted(n)
    public static void push(final ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(queue);
        Objects.requireNonNull(element);
        ensureCapacity(queue);
        queue.size++;
        queue.head = queue.head == 0 ? queue.elements.length - 1 : queue.head - 1;
        queue.elements[queue.head] = element;
    }

    // Pred: n >= 1 && queue != null
    // Post: n' == n && immutable(n) && R == a[n']
    public static Object peek(final ArrayQueueADT queue) {
        assert queue != null;
        assert size(queue) >= 1;
        return queue.elements[(queue.head + queue.size - 1) % queue.elements.length];
    }

    // Pred: n >= 1 && queue != null
    // Post: n' = n - 1 && immutable(n - 1) && R = a[1]
    public static Object remove(final ArrayQueueADT queue) {
        assert size(queue) >= 1;
        final int currentPos = (queue.head + queue.size - 1) % queue.elements.length;
        final Object result = queue.elements[currentPos];
        queue.elements[currentPos] = null;
        queue.size--;
        return result;
    }

    // Pred: element != null &&
    // существует 0 <= t <= n чисел i таких, что a[i].equals(element)
    // && queue != null
    // Post: n' = n && immutable(n) && R == t
    public static int count(final ArrayQueueADT queue, final Object element) {
        assert queue != null;
        assert element != null;
        int counter = 0;
        for (int i = 0; i < queue.size; i++) {
            if (queue.elements[(i + queue.head) % queue.elements.length].equals(element)) {
                counter++;
            }
        }
        return counter;
    }

    private static void ensureCapacity(final ArrayQueueADT queue) {
        if (queue.size == queue.elements.length) {
            final Object[] newElements = new Object[queue.elements.length * 2];
            System.arraycopy(queue.elements, queue.head, newElements, 0, queue.elements.length - queue.head);
            System.arraycopy(queue.elements, 0, newElements, queue.elements.length - queue.head, queue.head);
            queue.head = 0;
            queue.elements = newElements;
        }
    }
}

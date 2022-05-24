package queue;

import java.util.Arrays;
import java.util.Objects;

/*
    Model: a[1]..a[n]
    I: n >= 0 && for i = 1 .. n: a[i] != null

    immutable(n): for i = 1 .. n: a'[i] == a[i]
    shifted(n): for i = 2 .. n: a'[i] = a[i - 1]
*/

public class ArrayQueueModule {
    private static Object[] elements = new Object[16];
    private static int head;
    private static int size;

    // Pre: element != null
    // Post: n' = n + 1 && a[1] == element && shifted(n)
    public static void enqueue(Object element) {
        Objects.requireNonNull(element);
        ensureCapacity();
        size++;
        elements[(head + size - 1) % elements.length] = element;
    }

    // Pre: n >= 1
    // Post: R == a[n] && immutable(n) && n' == n
    public static Object element() {
        assert size >= 1;
        return elements[head];
    }

    // Pre: n >= 1
    // Post: R == a[n] && n' = n - 1 && immutable(n')
    public static Object dequeue() {
        assert size >= 1;
        Object result = elements[head];
        size--;
        elements[head] = null;
        head = (head + 1) % elements.length;
        return result;
    }

    // Pred: True
    // Post: R == n && n' == n && immutable(n)
    public static int size() {
        return size;
    }

    // Pred: True
    // Post: R == (n == 0) && n' == n && immutable(n)
    public static boolean isEmpty() {
        return size == 0;
    }

    // Pred: True
    // Post: n' = 0
    public static void clear() {
        while (!ArrayQueueModule.isEmpty()) {
            ArrayQueueModule.dequeue();
        }
    }

    // Pred: element != null
    // Post: n' = n + 1 && a[1] == element && shifted(n)
    public static void push(Object element) {
        Objects.requireNonNull(element);
        ensureCapacity();
        size++;
        head = head == 0 ? elements.length - 1 : head - 1;
        elements[head] = element;
    }

    // Pred: n >= 1
    // Post: n' == n && immutable(n) && R == a[n']
    public static Object peek() {
        assert size >= 1;
//        System.out.println(Arrays.toString(elements));
        return elements[(head + size - 1) % elements.length];
    }

    // Pred: n >= 1
    // Post: n' = n - 1 && immutable(n - 1) && R = a[1]
    public static Object remove() {
        assert size >= 1;
        int currentPos = (head + size - 1) % elements.length;
        Object result = elements[currentPos];
        elements[currentPos] = null;
        size--;
        return result;
    }

    // Pred: element != null &&
    // существует 0 <= t <= n чисел i таких, что a[i].equals(element)
    // Post: n' = n && immutable(n) && R == t
    public static int count(Object element) {
        assert element != null;
        int counter = 0;
        for (int i = 0; i < size; i++) {
            if (elements[(i + head) % elements.length].equals(element)) {
                counter++;
            }
        }
        return counter;
    }

    private static void ensureCapacity() {
        if (size == elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            System.arraycopy(elements, head, newElements, 0, elements.length - head);
            System.arraycopy(elements, 0, newElements, elements.length - head, head);
            head = 0;
            elements = newElements;
        }
    }

    public static void print() {
        System.out.println(Arrays.toString(elements));
    }
}
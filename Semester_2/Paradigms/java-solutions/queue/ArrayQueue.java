package queue;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[16];
    private int head;

    @Override
    protected void addLast(Object element) {
        ensureCapacity();
        elements[(head + size) % elements.length] = element;
    }

    @Override
    protected Object endRemove() {
        Object result = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        return result;
    }

    @Override
    protected Object getFirst() {
        return elements[head];
    }

    // Pred: element != null
    // Post: n' = n + 1 && a[1] == element && shifted(n)
    public void push(Object element) {
        Objects.requireNonNull(element);
        ensureCapacity();
        size++;
        head = head == 0 ? elements.length - 1 : head - 1;
        elements[head] = element;
    }

    // Pred: n >= 1
    // Post: n' == n && immutable(n) && R == a[n']
    public Object peek() {
        assert size >= 1;
        return elements[(head + size - 1) % elements.length];
    }

    // Pred: n >= 1
    // Post: n' = n - 1 && immutable(n - 1) && R = a[1]
    public Object remove() {
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
    public int count(Object element) {
        assert element != null;
        int counter = 0;
        for (int i = 0; i < size; i++) {
            if (elements[(i + head) % elements.length].equals(element)) {
                counter++;
            }
        }
        return counter;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            System.arraycopy(elements, head, newElements, 0, elements.length - head);
            System.arraycopy(elements, 0, newElements, elements.length - head, head);
            head = 0;
            elements = newElements;
        }
    }

    @Override
    public Queue createInstance() {
        return new ArrayQueue();
    }
}

package queue;

import java.util.function.Function;
import java.util.function.Predicate;

public class LinkedQueue extends AbstractQueue {
    private Node head;
    private Node tail;

    @Override
    protected void addLast(Object element) {
        final Node current = new Node(element);
        head = size == 0 ? current : head;
        if (size > 0) {
            tail.next = current;
        }
        tail = current;
    }

    @Override
    protected Object getFirst() {
        return head.value;
    }

    @Override
    protected Object endRemove() {
        Object result = head.value;
        head = head.next;
        return result;
    }

    @Override
    public Queue createInstance() {
        return new LinkedQueue();
    }

    private static class Node {
        private final Object value;
        private Node next;

        public Node(Object value) {
            this.value = value;
        }
    }
}

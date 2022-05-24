package queue;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size;

    @Override
    public void enqueue(final Object element) {
        Objects.requireNonNull(element);
        addLast(element);
        size++;
    }

    protected abstract void addLast(Object element);

    @Override
    public Object dequeue() {
        assert size >= 1;
        size--;
        return endRemove();
    }

    protected abstract Object endRemove();

    @Override
    public Object element() {
        assert size >= 1;
        return getFirst();
    }

    protected abstract Object getFirst();

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        while (!isEmpty()) {
            dequeue();
        }
    }

    public abstract Queue createInstance();

    @Override
    public Queue map(final Function<Object, Object> function) {
        Objects.requireNonNull(function);
        final Queue result = createInstance();
        final int currentSize = size();
        for (int i = 0; i < currentSize; i++) {
            final Object element = dequeue();
            result.enqueue(function.apply(element));
            enqueue(element);
        }
        return result;
    }

    @Override
    public Queue filter(final Predicate<Object> predicate) {
        Objects.requireNonNull(predicate);
        final Queue result = createInstance();
        final int currentSize = size();
        for (int i = 0; i < currentSize; i++) {
            final Object element = dequeue();
            if (predicate.test(element)) {
                result.enqueue(element);
            }
            enqueue(element);
        }
        return result;
    }
}

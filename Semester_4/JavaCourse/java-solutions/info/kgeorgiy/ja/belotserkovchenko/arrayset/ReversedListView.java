package info.kgeorgiy.ja.belotserkovchenko.arrayset;

import java.util.AbstractList;
import java.util.List;

public class ReversedListView<E> extends AbstractList<E> {
    private final List<E> source;

    public ReversedListView(List<E> source) {
        this.source = source;
    }

    public List<E> getSource() {
        return source;
    }

    @Override
    public E get(int index) {
        return source.get(source.size() - index - 1);
    }

    @Override
    public int size() {
        return source.size();
    }
}

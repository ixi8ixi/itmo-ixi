package info.kgeorgiy.ja.belotserkovchenko.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    private final List<E> data;
    final Comparator<E> comparator;

    private ArraySet(List<E> data, Comparator<E> comparator) {
        this.data = data;
        this.comparator = comparator;
    }

    public ArraySet() {
        this.data = List.of();
        this.comparator = null;
    }

    public ArraySet(Collection<E> collection) {
        this(collection, null);
    }

    // :NOTE: лишняя сортировка для сортированных коллекций
    public ArraySet(Collection<E> collection, Comparator<E> comparator) {
        if (collection instanceof SortedSet<E> &&
                Objects.equals(((SortedSet<E>) collection).comparator(), comparator)) {
            this.data = List.copyOf(collection);
        } else {
            NavigableSet<E> test = new TreeSet<>(comparator);
            test.addAll(collection);
            this.data = List.copyOf(test);
        }
        this.comparator = comparator;
    }

    private E getElement(int index) {
        if (index < 0 || index >= data.size()) {
            return null;
        }
        return data.get(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return Collections.binarySearch(data, (E) o, comparator) >= 0;
    }

    private int findIndex(E e, boolean less, boolean strict) {
        int index = Collections.binarySearch(data, e, comparator);
        if (index >= 0) {
            if (strict) {
                return index + (less ? -1 : 1); // fixme change here
            }
            return index;
        } else {
            index = (1 + index) * -1;
            return index + (less ? -1 : 0);
        }
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    private E searchImpl(E e, boolean less, boolean strict) {
        return getElement(findIndex(e, less, strict));
    }

    @Override
    public E lower(E e) {
        return searchImpl(e, true, true);
    }

    @Override
    public E floor(E e) {
        return searchImpl(e, true, false);
    }

    @Override
    public E ceiling(E e) {
        return searchImpl(e, false, false);
    }

    @Override
    public E higher(E e) {
        return searchImpl(e, false, true);
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }


    @Override
    public Iterator<E> iterator() {
        return data.iterator();
    }

    // a.descendingSet().descendingSet().descendingSet().descendingSet().descendingSet()
    // O(k) k - количество descendingSet
    @Override
    public NavigableSet<E> descendingSet() {
        if (data instanceof ReversedListView<E>) {
            return new ArraySet<>(((ReversedListView<E>) data).getSource(),
                    Collections.reverseOrder(comparator));
        }
        return new ArraySet<>(new ReversedListView<>(data), Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }


    private NavigableSet<E> subSetImpl(boolean fromFirst, E fromElement, boolean fromInclusive,
                                       boolean toLast, E toElement, boolean toInclusive) {
        int fromIndex = fromFirst ? 0 : findIndex(fromElement, false, !fromInclusive);
        int toIndex =  toLast ? data.size() : (findIndex(toElement, true, !toInclusive) + 1); // fixme less to true
        if (fromIndex > toIndex) {
            return new ArraySet<>();
        }
        return new ArraySet<>(data.subList(fromIndex, toIndex), comparator);
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (comparator == null) {
            @SuppressWarnings("unchecked")
                Comparable<E> from = (Comparable<E>) fromElement;
            if (from.compareTo(toElement) > 0) {
                throw new IllegalArgumentException("Expected fromKey < toKey");
            }
        } else if (comparator.compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("Expected fromKey < toKey");
        }
        return subSetImpl(false, fromElement, fromInclusive,
                false, toElement, toInclusive);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return subSetImpl(true, null, true,
                false, toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return subSetImpl(false, fromElement, inclusive,
                true, null, true);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    private E cGet(int index) {
        if (data.isEmpty()) {
            throw new NoSuchElementException();
        }
        return data.get(index);
    }

    @Override
    public E first() {
        return cGet(0);
    }

    @Override
    public E last() {
        return cGet(data.size() - 1);
    }

    @Override
    public int size() {
        return data.size();
    }
}

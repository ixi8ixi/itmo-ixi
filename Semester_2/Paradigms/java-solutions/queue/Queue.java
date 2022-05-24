package queue;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Queue {
    /*
        Model: a[1]..a[n]
        I: n >= 0 && for i = 1 .. n: a[i] != null

        immutable(n): for i = 1 .. n: a'[i] == a[i]
        shifted(n): for i = 2 .. n: a'[i] = a[i - 1]
    */

    // Pred: element != null
    // Post: n' = n + 1 && a[1] == element && shifted(n)
    void enqueue(Object element);

    // Pred: n >= 1
    // Post: R == a[n] && immutable(n) && n' == n
    Object element();

    // Pred: n >= 1
    // Post: R == a[n] && n' = n - 1 && immutable(n')
    Object dequeue();

    // Pred: True
    // Post: R == n && n' == n && immutable(n)
    int size();

    // Pred: True
    // Post: R == (n == 0) && n' == n && immutable(n)
    boolean isEmpty();

    // Pred: True
    // Post: n' = 0
    void clear();

    // Pred: function != null
    // Post: new Model: b[1] .. b[m] && n == m && for i = 1 .. n : b[i] = function(a[i])
    Queue map(Function<Object, Object> function);

    // Pred: function != null
    // Post: a.immutable(n) &&
    // new Model: R[1]..R[k] &&
    // n >= k &&
    // for i = 1 .. n:
    // if
    Queue filter(Predicate<Object> predicate);
}

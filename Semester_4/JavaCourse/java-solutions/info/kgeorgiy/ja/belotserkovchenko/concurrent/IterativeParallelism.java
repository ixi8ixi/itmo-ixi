package info.kgeorgiy.ja.belotserkovchenko.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements ListIP {
    private final ParallelMapper mapper;

    public IterativeParallelism() {
        this.mapper = null;
    }

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    private <T> List<Stream<T>> splitIntoStreams(List<T> list, int number) {
        ArrayList<Stream<T>> result = new ArrayList<>();

        int delta = list.size() / number;
        int tail = list.size() % number;

        for (int i = 0; i < number; i++) {
            int startPoint = i * delta;
            startPoint += Math.min(i, tail);

            int endPoint = startPoint + delta;
            if (i < tail) {
                endPoint += 1;
            }

            result.add(list.subList(startPoint, endPoint).stream());
        }

        return result;
    }

    private void startAndJoin(Thread[] threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                for (Thread thd: threads) {
                    thd.interrupt();
                    try {
                        thd.join();
                    } catch (InterruptedException ee) {
                        e.addSuppressed(ee);
                    }
                }
                throw new InterruptedException("Thread `" + thread + "` was interrupted: " + e);
            }
        }
    }

    private <T, U, R> R queryImpl(int threads, List<? extends T> values, Function<Stream<? extends T>, U> inThread,
                                  Function<Stream<? extends U>, R> inMain) throws InterruptedException {
        if (mapper != null) {
            List<U> intValues = mapper.map(inThread, splitIntoStreams(values, threads));
            return inMain.apply(intValues.stream());
        }

        Thread[] threadList = new Thread[threads];
        ArrayList<U> list = new ArrayList<>(Collections.nCopies(threads, null));

        int delta = values.size() / threads;
        int tail = values.size() % threads;

        for (int i = 0; i < threads; i++) {
            int finalI = i;
            threadList[i] = new Thread(() -> {
                int startPoint = finalI * delta;
                startPoint += Math.min(finalI, tail);

                int endPoint = startPoint + delta;
                if (finalI < tail) {
                    endPoint += 1;
                }

                list.set(finalI, inThread.apply(values.subList(startPoint, endPoint).stream()));
            });
        }

        startAndJoin(threadList);
        return inMain.apply(list.stream());
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values,
                         Comparator<? super T> comparator) throws InterruptedException {
        if (values.isEmpty()) {
            throw new NoSuchElementException("Values list is empty");
        }
        return queryImpl(threads, values, s -> s.max(comparator).orElse(null),
                s -> s.filter(Objects::nonNull).max(comparator).orElse(null));
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values,
                         Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, comparator.reversed());
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values,
                           Predicate<? super T> predicate) throws InterruptedException {
        return queryImpl(threads, values, s -> s.allMatch(predicate), s -> s.allMatch(p -> p));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values,
                           Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, predicate.negate());
    }

    @Override
    public <T> int count(int threads, List<? extends T> values,
                         Predicate<? super T> predicate) throws InterruptedException {
        return queryImpl(threads, values, s -> s.filter(predicate).toList().size(),
                s -> s.mapToInt(Integer::intValue).sum());
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return queryImpl(threads, values, s -> s.map(Object::toString).collect(Collectors.joining()),
                s -> s.collect(Collectors.joining()));
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values,
                              Predicate<? super T> predicate) throws InterruptedException {
        return queryImpl(threads, values, s -> s.filter(predicate).toList(),
                s -> s.collect(ArrayList::new, List::addAll, List::addAll));
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values,
                              Function<? super T, ? extends U> f) throws InterruptedException {
        return queryImpl(threads, values, s -> s.map(f).toList(),
                s -> s.collect(ArrayList::new, List::addAll, List::addAll));
    }
}

package info.kgeorgiy.ja.belotserkovchenko.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private final Thread[] threads;
    private final Deque<Runnable> queue = new ArrayDeque<>();

    private static class MapperHelper {
        private int value = 0;
        private RuntimeException exception = null;
        private final int notifyValue;

        MapperHelper(int notifyValue) {
            this.notifyValue = notifyValue;
        }

        synchronized void incrementCounter() {
            value++;
            if (value >= notifyValue) {
                notifyAll();
            }
        }

        synchronized void addException(RuntimeException e) {
            if (exception == null) {
                exception = e;
                return;
            }

            exception.addSuppressed(e);
        }

        // :NOTE: раз синхронизация необходима, то это все должно происходить внутри хелпера
        synchronized void waitCounterValue() throws InterruptedException {
            while (value < notifyValue) {
                wait();
            }
        }

        RuntimeException getException() {
            return exception;
        }
    }

    public ParallelMapperImpl(int threads) {
        this.threads = new Thread[threads];
        Runnable tr = () -> {
            try {
                while (!Thread.interrupted()) {
                    Runnable task;
                    synchronized (queue) {
                        while (queue.isEmpty()) {
                            queue.wait();
                        }

                        task = queue.poll();
                    }

                    task.run();
                }
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        };
        for (int i = 0; i < threads; ++i) {
            this.threads[i] = new Thread(tr);
        }
        runThreads();
    }

    private void runThreads() {
        for (Thread t : threads) {
            t.start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        ArrayList<R> result = new ArrayList<>(Collections.nCopies(args.size(), null));
        ArrayList<Runnable> taskList = new ArrayList<>(args.size());
        MapperHelper helper = new MapperHelper(args.size());
        for (int i = 0; i < args.size(); ++i) {
            int finalI = i;
            taskList.add(() -> {
                try {
                    result.set(finalI, f.apply(args.get(finalI)));
                } catch (RuntimeException e) {
                    helper.addException(e);
                } finally {
                    helper.incrementCounter();
                }
            });
        }
        synchronized (queue) {
            queue.addAll(taskList);
            queue.notifyAll();
        }

        helper.waitCounterValue();

        RuntimeException exception = helper.getException();
        if (exception != null) {
            throw exception;
        }

        return result;
    }

    @Override
    public void close() {
        for (Thread thread : threads) {
            thread.interrupt();
        }

        boolean isInterrupted = false;
        for (Thread thread : threads) {
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException e) {
                    isInterrupted = true;
                }
            }
        }

        if (isInterrupted) {
            Thread.currentThread().interrupt();
        }
    }
}

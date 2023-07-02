package info.kgeorgiy.ja.belotserkovchenko.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService downloadService;
    private final ExecutorService extractService;
    private final int perHost;
    private final Map<String, TaskManager> managers = new ConcurrentHashMap<>();

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloadService = Executors.newFixedThreadPool(downloaders);
        this.extractService = Executors.newFixedThreadPool(extractors);
        this.perHost = perHost;
    }

    private static class TaskManager {
        private int counter = 0;
        private final Queue<Runnable> delayedTaskQueue = new ArrayDeque<>();

        private synchronized void safeSubmit(Runnable task, ExecutorService downloadService, int perHost) {
            if (counter < perHost) {
                counter++;
                downloadService.submit(task);
            } else {
                delayedTaskQueue.add(task);
            }
        }

        private synchronized void resubmit(ExecutorService downloadService) {
            if (!delayedTaskQueue.isEmpty()) {
                downloadService.submit(delayedTaskQueue.poll());
            } else {
                counter--;
            }
        }

        private synchronized void deregisterAll(Phaser phaser) {
            while (!delayedTaskQueue.isEmpty()) {
                delayedTaskQueue.poll();
                phaser.arriveAndDeregister();
            }
            counter--;
        }
    }

    public static void main(String[] args) {
        String info = "usage: " + System.lineSeparator() + "\tWebCrawler url [depth [downloads [extractors [perHost]]]]";
        if (args.length == 0 || args.length > 5) {
            System.out.println(info);
            return;
        }

        int[] arguments = switch (args.length) {
            case 1, 2 -> new int[]{10, 10, 10};
            case 3 -> new int[]{Integer.parseInt(args[2]), 10, 10};
            case 4 -> new int[]{Integer.parseInt(args[2]), Integer.parseInt(args[3]), 10};
            case 5 -> new int[]{Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])};
            default -> throw new IllegalStateException("Unexpected value: " + args.length);
        };

        try (WebCrawler crawler =
                     new WebCrawler(new CachingDownloader(0), arguments[0], arguments[1], arguments[2])) {
            Result result = args.length == 1
                    ? crawler.download(args[0], 1)
                    : crawler.download(args[0], Integer.parseInt(args[1]));

            printStream(result.getDownloaded().stream(), "Downloaded", "*", s -> s);
            printStream(result.getErrors().entrySet().stream(),
                    "Errors", "*", s -> s.getKey() + " : " + s.getValue());
        } catch (IOException e) {
            System.out.println("Downloader error: " + e);
        }
    }

    private static <T> void printStream(Stream<T> stream, String title, String point, Function<T, String> mapFunc) {
        System.out.println(
                Stream.concat(Stream.of(title + ": "), stream.map(mapFunc))
                        .collect(Collectors.joining(System.lineSeparator() + " " + point + " ")));
    }

    private boolean isShutdown() {
        return downloadService.isShutdown() || extractService.isShutdown();
    }

    private Runnable makeTask(String url, Runnable inside, Map<String, IOException> exceptionMap, Phaser phaser) {
        phaser.register();
        return () -> {
            try {
                inside.run();
            } catch (UncheckedIOException e) {
                exceptionMap.putIfAbsent(url, e.getCause());
            } finally {
                phaser.arriveAndDeregister();
            }
        };
    }

    private Runnable downloadTask(int depth, String url, String host, Set<String> urlSet, Set<String> toNext,
                                  Map<String, IOException> exceptionMap, Phaser phaser) {
        Runnable inside = () -> {
            try {
                // :NOTE: Если уперлись в perHost лимит, то поток тупо простаивает. А мог бы заниматься поезным делом.
                Document document = downloader.download(url);
                if (depth > 1) {
                    // :NOTE: :
                    // RejectedExecutionException
                    try {
                        extractService.submit(extractTask(document, url, urlSet, toNext, exceptionMap, phaser));
                    } catch (RejectedExecutionException e) {
                        phaser.arriveAndDeregister();
                    }
                } else {
                    urlSet.add(url);
                }

            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } finally {
                TaskManager manager = managers.get(host);
                try {
                    manager.resubmit(downloadService);
                } catch (RejectedExecutionException e) {
                    manager.deregisterAll(phaser);
                }
            }
        };
        return makeTask(url, inside, exceptionMap, phaser);
    }

    private Runnable extractTask(Document document, String url, Set<String> urlSet, Set<String> toNext,
                                 Map<String, IOException> exceptionMap, Phaser phaser) {
        Runnable inside = () -> {
            try {
                List<String> urlList = document.extractLinks();
                toNext.addAll(urlList);
                urlSet.add(url);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
        return makeTask(url, inside, exceptionMap, phaser);
    }

    @Override
    public Result download(String url, int depth) {
        Set<String> urlSet = ConcurrentHashMap.newKeySet();
        Set<String> toNext = ConcurrentHashMap.newKeySet();
        Map<String, IOException> exceptions = new ConcurrentHashMap<>();
        Phaser phaser = new Phaser(1);

        toNext.add(url);

        for (int d = depth; d >= 1; d--) {
            if (isShutdown()) {
                break;
            }

            List<String> list = toNext.stream()
                    .filter(u -> !urlSet.contains(u) && !exceptions.containsKey(u)).toList();
            toNext.clear();

            for (String u : list) {
                try {
                    String host = URLUtils.getHost(u);
                    TaskManager manager = managers.computeIfAbsent(host, h -> new TaskManager());
                    Runnable task = downloadTask(d, u, host, urlSet, toNext, exceptions, phaser);
                    manager.safeSubmit(task, downloadService, perHost);
                } catch (MalformedURLException e) {
                    exceptions.putIfAbsent(u, e);
                } catch (RejectedExecutionException ee) {
                    break;
                }
            }

            phaser.arriveAndAwaitAdvance();
        }

        return new Result(urlSet.stream().toList(), exceptions);
    }

    @Override
    public void close() {
        downloadService.close();
        extractService.close();
    }
}

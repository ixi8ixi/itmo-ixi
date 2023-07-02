package info.kgeorgiy.ja.belotserkovchenko.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.*;

public class HelloUDPServer implements HelloServer {
    private ExecutorService executorService = null;
    private DatagramSocket socket = null;
    private ExecutorService receiveService = null;
    private static final int QUEUE_BUFF = 777;

    public static void main(String[] args) {
        String info = "usage:" + System.lineSeparator() + "\tHelloUDPServer port threads";
        if (args.length != 2) {
            System.out.println(info);
            return;
        }
        try (HelloUDPServer server = new HelloUDPServer()) {
            server.start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            System.out.println("Write something when you want to stop the server");
            new Scanner(System.in).next();
        }
    }

    private Runnable makeTask(byte[] data, int offset, int length, InetAddress address, int port) {
        String inputMessage = new String(data, offset, length, StandardCharsets.UTF_8);
        return () -> {
            try {
                String message = "Hello, " + inputMessage;
                // :NOTE: нужно  переиспользоть пакеты
                DatagramPacket sendPacket =
                        new DatagramPacket(message.getBytes(), 0, message.length(), address, port);

                socket.send(sendPacket);
            } catch (IOException e) {
                // Должен ли я вообще что-то делать в случае ошибки?
                // System.out.println("Error while send: " + e);
            }
        };
    }

    @Override
    public void start(int port, int threads) {
        executorService = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(threads + QUEUE_BUFF), new ThreadPoolExecutor.DiscardPolicy());
        receiveService = Executors.newSingleThreadExecutor();
        receiveService.submit(() -> {
            try {
                socket = new DatagramSocket(port);
                byte[] buff = new byte[socket.getSendBufferSize()];
                DatagramPacket receivePacket = new DatagramPacket(buff, buff.length);
                while (!socket.isClosed() && socket != null) {
                    try {
                        socket.receive(receivePacket);
                        executorService.submit(makeTask(receivePacket.getData(), receivePacket.getOffset(),
                                receivePacket.getLength(), receivePacket.getAddress(), receivePacket.getPort()));
                    } catch (IOException | RejectedExecutionException e) {
                        // System.out.println("Error while receive: " + e.getMessage());
                    }
                }
            } catch (SocketException e) {
                System.out.println("Unable to create socket: " + e.getMessage());
            }
        });
    }

    @Override
    public void close() {
        if (socket != null) {
            socket.close();
        }
        if (executorService != null) {
            executorService.shutdownNow();
            executorService.close();
        }
        if (receiveService != null) {
            receiveService.close();
        }
    }
}

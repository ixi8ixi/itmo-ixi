package info.kgeorgiy.ja.belotserkovchenko.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloUDPClient implements HelloClient {
    private static final int RECEIVE_TIMEOUT = 500;

    public static void main(String[] args) throws UnknownHostException {
        String info = "usage:" + System.lineSeparator() + "\tHelloUDPClient host port prefix threads requests";
        if (args.length != 5) {
            System.out.println(info);
            return;
        }
        new HelloUDPClient()
                .run(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
    }

    public static boolean checkResponse(String prefix, int thread, int request, String response) {
        // :NOTE: regex компилится на каждый запрос - очень дорого, лучше 1 компиляция на 1 тред
        String regex = "^Hello, " + prefix;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        if (!matcher.find()) {
            return false;
        }
        String[] numbers = response.substring(matcher.group().length()).split("_");
        if (numbers.length != 2) {
            return false;
        }
        try {
            if (Integer.parseInt(numbers[0]) != thread || Integer.parseInt(numbers[1]) != request) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // :NOTE: очень большая вложенность
    private static Runnable makeTask(int thread, int size, InetAddress inetAddress, int port, String prefix,
                                     Phaser phaser, ExecutorService service) {
        phaser.register();
        return () -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(RECEIVE_TIMEOUT);
                byte[] buff = new byte[socket.getSendBufferSize()];
                for (int i = 1; i <= size; i++) {
                    String message = prefix + thread + "_" + i;

                    DatagramPacket receivePacket = new DatagramPacket(buff, buff.length);
                    // :NOTE: таска ничего не должна знать про то, где она крутится
                    while (!service.isShutdown()) {
                        try {
                            System.out.println("Sending query: `" + message + "`...");
                            // :NOTE: нужно переиспользовать пакеты в рамках треда
                            DatagramPacket sendPacket =
                                    new DatagramPacket(message.getBytes(), 0, message.length(), inetAddress, port);

                            socket.send(sendPacket);
                            try {
                                socket.receive(receivePacket);
                                String response = new String(receivePacket.getData(),
                                        receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8);
                                if (checkResponse(prefix, thread, i, response)) {
                                    System.out.println(
                                            "Response for query `" + message +
                                                    "`was received successfully: `" + response + "`");
                                    break;
                                } else {
                                    System.out.println(
                                            "Invalid response for query `" + message +
                                                    "` : `" + response + "`");
                                }
                            } catch (SocketTimeoutException e) {
                                System.out.println("Query `" + message + "` response timed out, resending...");
                            } catch (IOException e) {
                                System.out.println("IOException `" + e + "` occurs while receiving response to `"
                                        + message + "`, resending...");
                            }
                        } catch (IOException e) {
                            System.out.println("IOException `" + e + "` occurs while sending `"
                                    + message + "`, resending...");
                        }
                    }
                }
            } catch (SocketException e) {
                System.out.println("Unable to create socket: " + e.getMessage());
            } finally {
                phaser.arriveAndDeregister();
            }
        };
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        ExecutorService service = Executors.newFixedThreadPool(threads);
        // :NOTE: оверкилл
        Phaser phaser = new Phaser(1);
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            for (int i = 1; i <= threads; i++) {
                service.submit(makeTask(i, requests, inetAddress, port, prefix, phaser, service));
            }
        } catch (UnknownHostException e) {
            System.out.println("Invalid host: " + e.getMessage());
        }
        phaser.arriveAndAwaitAdvance();
        service.close();
    }
}

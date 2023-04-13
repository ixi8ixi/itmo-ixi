package info.kgeorgiy.ja.belotserkovchenko.walk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RecursiveWalk {
    private static final String errorHash = "0".repeat(64);

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.out.println("Usage: java RecursiveWalker `input_file` `output_file`");
            return;
        }
        try {
            Path inputFile = Paths.get(args[0]);
            Path outputFile = Paths.get(args[1]);

            walk(inputFile, outputFile);
        } catch (InvalidPathException e) {
            System.out.println("Invalid input or output path: " + e.getMessage());
        }
    }

    private static void walk(Path inputFile, Path outputFile) {
        try {
            Path outputParent = outputFile.getParent();
            if (outputParent != null) {
                Files.createDirectories(outputFile.getParent());
            }
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            try (BufferedReader filesList = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
                 BufferedWriter hashSum = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {

                byte[] buff = new byte[512];
                FileVisitor<Path> visitor = new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String result;

                        try (BufferedInputStream fileReader = new BufferedInputStream(Files.newInputStream(file))) {
                            int len;
                            while ((len = fileReader.read(buff)) != -1) {//(true) {
                                messageDigest.update(buff, 0, len);
                            }
                            result = getHexString(messageDigest.digest());
                        } catch (IOException | SecurityException e) {
                            result = null;
                        }

                        if (result != null) {
                            writeFileHash(hashSum, result, file.toString());
                        } else {
                            writeError(hashSum, file.toString());
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        writeError(hashSum, file.toString());
                        return FileVisitResult.CONTINUE;
                    }
                };

                while (filesList.ready()) {
                    String fileName = filesList.readLine();
                    messageDigest.reset();
                    try {
                        Path path = Paths.get(fileName);
                        Files.walkFileTree(path, visitor);
                    } catch (InvalidPathException | SecurityException e) {
                        writeError(hashSum, fileName);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while reading file list or writing to hash list: " + e.getMessage());
            } catch (SecurityException ee) {
                System.out.println("Security exception: " + ee.getMessage());
            }
        } catch (NoSuchAlgorithmException ignored) {
        } catch (IOException e) {
            System.out.println("Unable to create directory: " + outputFile.getParent());
        } catch (SecurityException ee) {
            System.out.println("Security exception: " + ee.getMessage());
        }
    }

    private static void writeFileHash(BufferedWriter bw, String hash, String file) throws IOException {
        bw.write(hash + " " + file);
        bw.newLine();
    }

    private static void writeError(BufferedWriter bw, String file) throws IOException {
        writeFileHash(bw, errorHash, file);
    }

    private static String getHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(String.format("%02x", aByte));
        }
        return sb.toString();
    }
}


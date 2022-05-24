import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class WordStatInput {
    public static void main(String[] args) {
        String inputFile = args[0];
//        String inputFile = "input.txt";
//        String outputFile = "output.txt";
        String outputFile = args[1];
        Map<String, Integer> words = new LinkedHashMap<>();
        try {
            MyScanner liner = new MyScanner(new FileInputStream(inputFile));
            try {
                while (liner.hasNextLine()) {
                    liner.setMode(1);
                    while (liner.hasNextTokenInLine()) {
                        String word = liner.next();
                        word = word.toLowerCase();
                        if (words.containsKey(word)) {
                            words.put(word, words.get(word) + 1);
                        } else {
                            words.put(word, 1);
                        }
                    }
                    liner.nextLine();
                }

            } finally {
                liner.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Cannot read input file: " + e.getMessage());
        }

        try {
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile),
                            StandardCharsets.UTF_8
                    )
            );
            try {
                for (String key : words.keySet()) {
                    out.write(key + " " + words.get(key) + "\n");
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Cannot write into output file: " + e.getMessage());
        }
    }
}

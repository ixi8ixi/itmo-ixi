import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wspp {
    public static void main(String[] args) {
        String inputFile = args[0];
        String outputFile = args[1];
        Map<String, IntList> words = new LinkedHashMap<>();
        try {
            int wordCount = 0;
            MyScanner liner = new MyScanner(new FileInputStream(inputFile));
            liner.setMode(1);
            try {
                while (liner.hasNextLine()) {
                    while (liner.hasNextTokenInLine()) {
                        wordCount++;
                        String word = liner.next();
                        word = word.toLowerCase();
                        if (!words.containsKey(word)) {
                            words.put(word, new IntList());
                        }
                        words.get(word).addEntry(wordCount);
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
                    words.get(key).cutEnd();
                    int currentAmount = words.get(key).getAmount();
                    int[] currentEntryNumbers = words.get(key).getEnt();
                    out.write(key + " " + currentAmount);
                    for (int number: currentEntryNumbers) {
                        out.write(" " + number);
                    }
                    out.write("\n");
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Cannot write into output file: " + e.getMessage());
        }
    }
}

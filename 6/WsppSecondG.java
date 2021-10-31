import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class WsppSecondG {
    public static void main(String[] args) {
        String inputFile = args[0];
        String outputFile = args[1];
        Map<String, IntList> words = new LinkedHashMap<>();
        try {
            int wordCount = 0;
            MyScanner liner = new MyScanner(new FileInputStream(inputFile));
            try {
                while (liner.hasNextLine()) {
                    MyScanner inner = new MyScanner(liner.nextLine());
                    inner.setMode(1);
                    try {
                        while (inner.hasNext()) {
                            wordCount++;
                            String word = inner.next();
                            word = word.toLowerCase();
                            if (words.containsKey(word)) {
                                words.get(word).addEntryNumber(wordCount);
                            } else {
                                words.put(word, new IntList(wordCount));
                            }
                        }
                        for (String key : words.keySet()) {
                            words.get(key).falseParity();
                        }
                    } finally {
                        inner.close();
                    }
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
                    int[] currentEntryNumbers = words.get(key).getEntryNumbers();
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
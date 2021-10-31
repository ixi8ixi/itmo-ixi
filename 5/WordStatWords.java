import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WordStatWords {
    public static void main(String[] args) {
        String inputFile = args[0];
        String outputFile = args[1];
        Tree words = new Tree();
        try {
            MyScanner in = new MyScanner(new FileInputStream(inputFile));
            in.setMode(1);
            try {
                while (in.hasNext()) {
                    String word = in.next();
                    word = word.toLowerCase();
                    words.appendNode(word);
                }
            } finally {
                in.close();
            }    
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
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
                words.drawTreeToFile(words.getFirstNode(), out);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Cannot write into output file: " + e.getMessage());
        }
    }
}
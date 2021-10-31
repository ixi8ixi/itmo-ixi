import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WordStatWords {
    static boolean isNormChar(char symbol) {
        return (Character.getType(symbol) == Character.DASH_PUNCTUATION ||
            symbol == '\'' ||
            Character.isLetter(symbol)
        );
    }

    public static void main(String[] args) {
        Tree words = new Tree();
        try {
            StringBuilder nWord = new StringBuilder();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(args[0]),
                        StandardCharsets.UTF_8
                )
            );
            try {
                boolean isPrevNorm = false;
                while (true) { 
                    int symbol = in.read();
                    char symba = (char) symbol;

                    // Break if end of file
                    if (symbol == -1) {
                        if (isPrevNorm) {
                            String word = nWord.toString();
                            word = word.toLowerCase();
                            words.appendNode(word);
                        }
                        break;
                    }

                    if (isNormChar(symba)) {
                        isPrevNorm = true;
                        nWord.append(symba);
                    } else if (isPrevNorm && !isNormChar(symba)){
                        String word = nWord.toString();
                        word = word.toLowerCase();
                        words.appendNode(word);
                        nWord = new StringBuilder("");
                        isPrevNorm = false;
                    }
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
                    new FileOutputStream(args[1]),
                        StandardCharsets.UTF_8
                )
            );
            try {
                words.drawTreeToFile(words.getFirstNode(), out);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Cannot write into output file: ");
        }
    }
}
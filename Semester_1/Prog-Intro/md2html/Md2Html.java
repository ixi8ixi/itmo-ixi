// TODO убрать касты к дочерним классам

package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Md2Html {
    public static void main(String[] args) {
        String inputFile = args[0];
        String outputFile = args[1];
//        String inputFile = "input.txt";
//        String outputFile = "output.txt";
        StringBuilder out = new StringBuilder();
        try {
            MarkdownScanner input = new MarkdownScanner(new FileInputStream(inputFile));
            try {
                Md2HtmlMaker test = new Md2HtmlMaker(input);
                out = test.createSheet();
            } finally {
                input.close();
            }
        } catch (IOException e) {
            System.err.println("Cannot read input file: " + e.getMessage());
        }
        try {
            BufferedWriter output = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile),
                            StandardCharsets.UTF_8
                    )
            );
            try {
                output.write(out.toString());
            } finally {
                output.close();
            }
        } catch (IOException e) {
            System.err.println("Cannot write into output file: " + e.getMessage());
        }
    }
}

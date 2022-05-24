package expression.generic;

import expression.exceptions.ParsingException;

import java.util.Arrays;

public class GenericMain {
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Here must be two arguments");
        }
        if (args[0].charAt(0) != '-') {
            throw new IllegalArgumentException("First argument should starts with `-`");
        }
        try {
            System.out.println(Arrays.deepToString(
                    new GenericTabulator().tabulate(
                            args[0].substring(1), args[1], -2, 2, -2, 2, -2, 2)
                    )
            );
        } catch (ParsingException e) {
            System.err.println("Parsing error: " + e.getMessage());
        }
    }
}

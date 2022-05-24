package expression.generic;

import expression.TripleExpression;
import expression.exceptions.ParsingException;
import expression.generic.types.*;
import expression.parser.ExpressionParser;

import java.util.function.Supplier;

public class GenericTabulator implements Tabulator {
    public static <T> TripleExpression<T> parseExpression(String expr, Supplier<AbstractType<T>> valueInstanceCreator)
            throws ParsingException {

        return new ExpressionParser<T>().parse(expr, valueInstanceCreator);
    }

    @Override
    public Object[][][] tabulate(String mode, String expression,
                                 int x1, int x2, int y1, int y2, int z1, int z2) throws ParsingException {
        if (x2 < x1 || y2 < y1|| z2 < z1) {
            throw new IllegalArgumentException("One of the top borders is less than bottom");
        }
        TripleExpression<?> parsedExpression = switch (mode) {
            case "i" -> parseExpression(expression, CheckedInteger::new);
            case "d" -> parseExpression(expression, MyDouble::new);
            case "bi" -> parseExpression(expression, MyBigInteger::new);
            case "u" -> parseExpression(expression, UncheckedInteger::new);
            case "l" -> parseExpression(expression, MyLong::new);
            case "f" -> parseExpression(expression, MyFloat::new);
            default -> throw new IllegalArgumentException("Unknown type: " + mode);
        };
        int xDelta = x2 - x1 + 1;
        int yDelta = y2 - y1 + 1;
        int zDelta = z2 - z1 + 1;
        Object[][][] result = new Object[xDelta][yDelta][zDelta];
        for (int i = 0; i < xDelta; i++) {
            for (int j = 0; j < yDelta; j++) {
                for (int k = 0; k < zDelta; k++) {
                    try {
                        result[i][j][k] = parsedExpression.evaluate(i + x1, j + y1, k + z1).getValue();
                    } catch (ArithmeticException e) {
                    }

                }
            }
        }
        return result;
    }
}

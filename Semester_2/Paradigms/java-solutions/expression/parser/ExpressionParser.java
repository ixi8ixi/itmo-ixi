package expression.parser;

import expression.*;
import expression.exceptions.*;
import expression.generic.types.AbstractType;

import java.util.function.Supplier;

public class ExpressionParser<N> {
    public TripleExpression<N> parse(String expression, Supplier<AbstractType<N>> valueInstanceCreator) throws ParsingException {
        return new ExpressionParserFilling<N>(new StringSource(expression), valueInstanceCreator).parse();
    }

    public static class ExpressionParserFilling<N> extends BaseParser {
        private final Supplier<AbstractType<N>> valueInstanceCreator;

        protected ExpressionParserFilling(CharSource source, Supplier<AbstractType<N>> valueInstanceCreator) {
            super(source);
            this.valueInstanceCreator = valueInstanceCreator;
        }

        public TripleExpression<N> parse() throws ParsingException {
            skipWhitespace();
            TripleExpression<N> result = parseMinMax();
            if (!eof()) {
                throw error("End of file expected, actual: '" + take() + "'");
            }
            return result;
        }

        private boolean takeWhitespace() {
            return Character.isWhitespace(ch);
        }

        private void skipWhitespace() {
            while (takeWhitespace()) {
                take();
            }
        }

        private void takeDigits(StringBuilder in) {
            while (between('0', '9')) {
                in.append(take());
            }
        }

        public Const<N> parseConst(boolean negative) {
            StringBuilder result = new StringBuilder();
            if (negative) {
                result.append('-');
            }
            if (between('0', '9')) {
                takeDigits(result);
            }
            skipWhitespace();
            return new Const<>(Integer.parseInt(result.toString()), valueInstanceCreator);
        }

        public Variable<N> parseVar() throws ParsingException {
            char name = take();
            skipWhitespace();
            return switch (name) {
                case 'x' -> new Variable<>("x", valueInstanceCreator);
                case 'y' -> new Variable<>("y", valueInstanceCreator);
                case 'z' -> new Variable<>("z", valueInstanceCreator);
                default -> throw error("Unexpected variable name: " + name);
            };
        }

        public TripleExpression<N> parseUnary() throws ParsingException {
            expect("count");
            skipWhitespace();
            return new Count<>(parseValue());
        }

        private TripleExpression<N> parseBrackets() throws ParsingException {
            TripleExpression<N> result = parseMinMax();
            expect(')');
            skipWhitespace();
            return result;
        }

        public TripleExpression<N> parseValue() throws ParsingException {
            skipWhitespace();
            if (between('x', 'z')) {
                return parseVar();
            } else if (take('-')) {
                if (between('0', '9')) {
                    return parseConst(true);
                } else {
                    return new Negate<>(parseValue());
                }
            } else if (between('0', '9')) {
                return parseConst(false);
            } else if (test('c')) {
                return parseUnary();
            } else if (take('(')) {
                return parseBrackets();
            }
            throw error("Unexpected symbol: " + take());
        }

        public TripleExpression<N> parseTerm() throws ParsingException {
            skipWhitespace();
            TripleExpression<N> result = parseValue();
            while (isTerm()) {
                if (take('*')) {
                    result = new Multiply<>(result, parseValue());
                } else if (take('/')) {
                    result = new Divide<>(result, parseValue());
                } else {
                    throw error("'*' or '/' expected, actual: '" + take() + "'");
                }
                skipWhitespace();
            }
            return result;
        }

        public boolean isTerm() {
            return !test('+') && !test('-') && isSum();
        }

        public TripleExpression<N> parseSum() throws ParsingException {
            skipWhitespace();
            TripleExpression<N> result = parseTerm();
            while (isSum()) {
                if (take('+')) {
                    result = new Add<>(result, parseTerm());
                } else if (take('-')) {
                    result = new Subtract<>(result, parseTerm());
                } else {
                    throw error("'+' or '-' expected, actual: '" + take() + "'");
                }
                skipWhitespace();
            }
            return result;
        }

        public boolean isSum() {
            return isMinMax() && !test('m');
        }

        public TripleExpression<N> parseMinMax() throws ParsingException {
            skipWhitespace();
            TripleExpression<N> result = parseSum();
            while (isMinMax()) {
                expect('m');
                if (take('i')) {
                    expect('n');
                    result = new MinOperation<>(result, parseSum());
                } else if (take('a')) {
                    expect('x');
                    result = new MaxOperation<>(result, parseSum());
                } else {
                    throw error("Expected 'i' or 'a', actual: '" + take() + "'");
                }
                skipWhitespace();
            }
            return result;
        }

        private boolean isMinMax() {
            return !eof() && !test(')');
        }
    }
}
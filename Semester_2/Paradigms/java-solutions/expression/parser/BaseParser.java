package expression.parser;

import expression.exceptions.ParsingException;

public class BaseParser {
    private static final char END = '\0';
    private final CharSource source;
    protected char ch = 0xffff;

    protected BaseParser(CharSource source) {
        this.source = source;
        take();
    }

    protected boolean test(char expected) {
        return ch == expected;
    }

    protected char take() {
        final char result = ch;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected boolean take(char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected void expect(char expected) throws ParsingException {
        if (!take(expected)) {
            throw error("Expected '" + expected + "', found '" + ch + "'");
        }
    }

    protected void expect(String value) throws ParsingException {
        for (final char c : value.toCharArray()) {
            expect(c);
        }
    }

    protected boolean eof() {
        return take(END);
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }

    protected ParsingException error(String message) {
        return source.error(message);
    }
}

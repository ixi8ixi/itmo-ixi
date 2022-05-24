package expression.parser;

import expression.exceptions.ParsingException;

public class StringSource implements CharSource {
    private final String DATA;
    private int position;

    public StringSource(String string) {
        this.DATA = string;
    }

    @Override
    public boolean hasNext() {
        return position < DATA.length();
    }

    @Override
    public char next() {
        return DATA.charAt(position++);
    }

    @Override
    public ParsingException error(String message) {
        return new ParsingException(String.format("%s : %d : %s", DATA.substring(0, position), position, message));
    }
}

package expression.parser;

import expression.exceptions.ParsingException;

public interface CharSource {
    boolean hasNext();
    char next();
    ParsingException error(String message);
}

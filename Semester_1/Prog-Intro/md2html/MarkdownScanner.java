package md2html;

import markup.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class MarkdownScanner {
    private final Reader in;
    private final int bufferSize = 512;
    private final char[] charBuffer = new char[bufferSize];
    private int currentPosition;
    private int currentBufferSize;
    private boolean isStrikeout = false;

    public MarkdownScanner(InputStream input) throws IOException{
        in = (new InputStreamReader(input, StandardCharsets.UTF_8));
        currentBufferSize = in.read(charBuffer);
        currentPosition = 0;
    }

    private void nextChar() throws IOException {
        currentPosition++;
        if (currentPosition == currentBufferSize) {
            currentBufferSize = in.read(charBuffer);
            currentPosition = 0;
        }
    }

    private boolean isLineSeparator(char symbol) {
        return symbol == '\n' ||
                symbol == '\r' ||
                symbol == '\u000B' ||
                symbol == '\u000C' ||
                symbol == '\u0085' ||
                symbol == '\u2028' ||
                symbol == '\u2029';
    }

    public boolean isTextSymbol(char symbol) {
        return (symbol != '*' &&
                symbol != '_' &&
                symbol != '`' &&
                !isLineSeparator(symbol));
    }

    public boolean isSpecialSymbol(char symbol) {
        return (symbol == '<' ||
                symbol == '>' ||
                symbol == '&'
        );
    }

    public Text nextText() throws IOException {
        StringBuilder text = new StringBuilder();
        while (isTextSymbol(charBuffer[currentPosition]) && currentBufferSize != -1) {
            if (charBuffer[currentPosition] == '\\') {
                nextChar();
            }
            if (charBuffer[currentPosition] == '-') {
                nextChar();
                if (charBuffer[currentPosition] != '-' || currentBufferSize == -1) {
                    text.append('-');
                } else {
                    isStrikeout = true;
                    return new Text(text.toString());
                }
            }
            if (isSpecialSymbol(charBuffer[currentPosition])) {
                switch (charBuffer[currentPosition]) {
                    case '<':
                        text.append("&lt;");
                        break;
                    case '>':
                        text.append("&gt;");
                        break;
                    case '&':
                        text.append("&amp;");
                        break;
                }
                nextChar();
                continue;
            }
            text.append(charBuffer[currentPosition]);
            nextChar();
        }
        return new Text(text.toString());
    }

    public MdSymbol nextMdSymbol() throws IOException {
        char check = charBuffer[currentPosition];
        nextChar();
        switch (check) {
            case '-':
                isStrikeout = false;
                return new MdSymbol(MdSymbolType.STRIKEOUT_SYMBOL);
            case '*':
                return new MdSymbol(MdSymbolType.EMPHASIS_SYMBOL_STAR);
            case '_':
                return new MdSymbol(MdSymbolType.EMPHASIS_SYMBOL_UNDERLINE);
            case '`':
                return new MdSymbol(MdSymbolType.CODE_SYMBOL);
            default:
                throw new AssertionError("The text being read is not a markup character");
        }
    }


    public LineSeparator nextLineSeparator() throws IOException {
        char check = charBuffer[currentPosition];
        nextChar();
        if ((check == '\n' && charBuffer[currentPosition] == '\r' ||
                check == '\r' && charBuffer[currentPosition] == '\n' ) &&
                currentBufferSize != -1) {
            nextChar();
        }
        return new LineSeparator();
    }

    public InTextHighlighting next() throws IOException {
        if (isTextSymbol(charBuffer[currentPosition]) && !isStrikeout) {
            return nextText();
        } else if (isLineSeparator(charBuffer[currentPosition])) {
            return nextLineSeparator();
        } else {
            return nextMdSymbol();
        }
    }

    public boolean hasNext() {
        return currentBufferSize != -1;
    }

    public void toNextParagraph() throws IOException {
        while (isLineSeparator(charBuffer[currentPosition]) && hasNext()) {
            nextChar();
        }
    }

    public void close() throws IOException {
        in.close();
    }
}
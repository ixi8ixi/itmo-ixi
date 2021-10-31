import java.io.*;
import java.nio.charset.StandardCharsets;

public class MyScanner {
    private Reader in;
    private int bufferSize = 512;
    private char[] charBuffer = new char[bufferSize];
    private int currentPosition;
    private int currentBufferSize;
    private boolean letterDashApoMode = false;

    public MyScanner(InputStream input) throws IOException{
        in = new InputStreamReader(input, StandardCharsets.UTF_8);
        currentBufferSize = in.read(charBuffer);
        currentPosition = 0;
    }

    public MyScanner(String input) throws IOException{
        in = new StringReader(input);
        currentBufferSize = in.read(charBuffer);
        currentPosition = 0;
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

    private void nextChar() throws IOException {
        currentPosition++;
        if (currentPosition == currentBufferSize) {
            currentBufferSize = in.read(charBuffer);
            currentPosition = 0;
        }
    }

    private boolean isWordChar(char symbol) {
        return (Character.getType(symbol) == Character.DASH_PUNCTUATION ||
                symbol == '\'' ||
                Character.isLetter(symbol)
        );
    }

    public boolean isNormChar(char symbol) {
        return (Character.getType(symbol) == Character.DASH_PUNCTUATION ||
                symbol == '\'' ||
                Character.isLetter(symbol)
        );
    }

    public void setMode(int mode) {
        // 0 - all whitespace characters are delimiters
        // 1 - all non-letter, apostrophes and dashes characters are delimiters
        if (mode == 0) {
            letterDashApoMode = false;
        } else if (mode == 1) {
            letterDashApoMode = true;
        }
    }

    public boolean hasNextLine() {
        return currentBufferSize != -1;
    }

    public String nextLine() throws IOException {
        StringBuilder line = new StringBuilder();
        while (!isLineSeparator(charBuffer[currentPosition]) && currentBufferSize != -1) {
            line.append(charBuffer[currentPosition]);
            nextChar();
        }
        char test = charBuffer[currentPosition];
        nextChar();
        if ((test == '\r' && charBuffer[currentPosition] == '\n') ||
                (test == '\n' && charBuffer[currentPosition] == '\r')) {
            nextChar();
        }
        return line.toString();
    }

    public boolean hasNext() throws IOException {
        if (!letterDashApoMode) {
            while (Character.isWhitespace(charBuffer[currentPosition]) && currentBufferSize != -1) {
                nextChar();
            }
        } else {
            while (!isWordChar(charBuffer[currentPosition]) && currentBufferSize != -1) {
                nextChar();
            }
        }
        return currentBufferSize != -1;
    }

    public String next() throws IOException {
        hasNext();
        StringBuilder token = new StringBuilder();
        if (!letterDashApoMode) {
            while (!Character.isWhitespace(charBuffer[currentPosition]) && currentBufferSize != -1) {
                token.append(charBuffer[currentPosition]);
                nextChar();
            }
        } else {
            while (isNormChar(charBuffer[currentPosition]) && currentBufferSize != -1) {
                token.append(charBuffer[currentPosition]);
                nextChar();
            }
        }
        return token.toString();
    }

    public int nextInt() throws IOException {
        String token = next();
        if (token.startsWith("0x") || token.startsWith("0X")) {
            return Integer.parseUnsignedInt(token.substring(2), 16);
        } else {
            return Integer.parseInt(token);
        }
    }

    public void close() throws IOException {
        in.close();
    }
}

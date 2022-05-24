package game;

import java.util.Map;

public class HexPosition implements Position {
    private final Cell[] field;
    private final int FIELD_HEIGHT;
    private final HexBoard board;

    private final int NUMBER_LEN;
    private final String HEX_TOP;

    private static final Map<Cell, String> CELL_TO_STRING = Map.of(
            Cell.E, ".",
            Cell.X, "X",
            Cell.O, "0"
    );

    public HexPosition(HexBoard board, int m, Cell[] field) {
        FIELD_HEIGHT = m;
        this.field = field;
        this.board = board;

        NUMBER_LEN = String.valueOf(FIELD_HEIGHT).length();
        StringBuilder sb = new StringBuilder("\\ ");
        for (int i = 0; i < FIELD_HEIGHT; i++) {
            sb.append("/ \\ ");
        }
        HEX_TOP = sb.toString();
    }

    @Override
    public Cell getTurn() {
        return board.getTurn();
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < FIELD_HEIGHT
                && 0 <= move.getCol() && move.getCol() < FIELD_HEIGHT
                && getCell(move.getRow(), move.getCol()) == Cell.E
                && getTurn() == move.getValue();
    }

    @Override
    public Cell getCell(int row, int column) {
        int hexNumber = row * FIELD_HEIGHT + column;
        return field[hexNumber];
    }

    private char getDigit(int number, int pos) {
        String num = String.valueOf(number);
        int len = num.length();
        if (len - 1 >= pos) {
            return num.charAt(len - pos - 1);
        } else {
            return ' ';
        }
    }

    private String drawLeftNumber(int number, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len - String.valueOf(number).length(); i++) {
            sb.append(' ');
        }
        sb.append(number);
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        // Draw top
        for (int i = NUMBER_LEN - 1; i >= 0; i--) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                sb.append("   ");
                sb.append(getDigit(j + 1, i));
            }
            sb.append(System.lineSeparator());
        }
        // Main field
        sb.append("  ");
        sb.append(HEX_TOP.substring(2));
//        sb.append(System.lineSeparator());
        for (int i = 0; i < FIELD_HEIGHT; i++) {
            sb.append(System.lineSeparator());
            sb.append(drawLeftNumber(i + 1, i * 2 + 1));
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                sb.append("| ");
                sb.append(CELL_TO_STRING.get(field[i * FIELD_HEIGHT + j])).append(" ");
            }
            sb.append("|");
            sb.append(System.lineSeparator());
            for (int j = 0; j < i * 2 + 2; j++) {
                sb.append(" ");
            }
            sb.append(HEX_TOP);
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}

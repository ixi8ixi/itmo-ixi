package game;

import java.util.Map;

public class TicTacToePosition implements Position {
    private final Cell[][] field;
    private final int FIELD_HEIGHT;
    private final int FIELD_WIDTH;
    private final TicTacToeBoard board;

    private final int LEFT_LEN;
    private final int TOP_LEN;

    private static final Map<Cell, String> CELL_TO_STRING = Map.of(
            Cell.E, ".",
            Cell.X, "X",
            Cell.O, "0"
    );

    public TicTacToePosition(TicTacToeBoard board, int m, int n, Cell[][] field) {
        FIELD_HEIGHT = m;
        FIELD_WIDTH = n;
        this.field = field;
        this.board = board;

        LEFT_LEN = String.valueOf(FIELD_HEIGHT).length();
        TOP_LEN = String.valueOf(FIELD_WIDTH).length();
    }

    @Override
    public Cell getTurn() {
        return board.getTurn();
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < FIELD_HEIGHT
                && 0 <= move.getCol() && move.getCol() < FIELD_WIDTH
                && field[move.getRow()][move.getCol()] == Cell.E
                && getTurn() == move.getValue();
    }

    @Override
    public Cell getCell(int row, int column) {
        return field[row][column];
    }

    public String drawCell(String string, int len) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len - string.length(); i++) {
            result.append(" ");
        }
        result.append(string);
        return result.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(drawCell("", LEFT_LEN));
        sb.append("|");
        for (int i = 0; i < FIELD_WIDTH; i++) {
            sb.append(drawCell(String.valueOf(i + 1), TOP_LEN));
            sb.append("|");
        }
        for (int i = 0; i < FIELD_HEIGHT; i++) {
            sb.append(System.lineSeparator());
            sb.append(drawCell(String.valueOf(i + 1), LEFT_LEN));
            sb.append("|");
            for (int j = 0; j < FIELD_WIDTH; j++) {
                sb.append(drawCell(CELL_TO_STRING.get(field[i][j]), TOP_LEN));
                sb.append("|");
            }
        }
        return sb.toString();
    }
}

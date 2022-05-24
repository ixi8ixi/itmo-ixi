package game;

import java.util.Arrays;

public class TicTacToeBoard implements Board {
    private final Cell[][] field;
    private final int FIELD_HEIGHT;
    private final int FIELD_WIDTH;
    private final int BOARD_AREA;
    private final int WIN_ROW;
    private Cell turn;
    private final TicTacToePosition POSITION;

    private int moveCounter;

    public TicTacToeBoard(int m, int n, int k) {
        printRules();
        field = new Cell[m][n];
        FIELD_HEIGHT = m;
        FIELD_WIDTH = n;
        BOARD_AREA = FIELD_HEIGHT * FIELD_WIDTH;
        POSITION = new TicTacToePosition(this, m, n, field);
        WIN_ROW = k;
        clear();
    }

    @Override
    public void printRules() {
        System.out.println("In TicTacToe moves must be entered as 2 Integer numbers r and c, ");
        System.out.println("which define the cell at the intersection of row r and column c: ");
        System.out.println("     COLUMN");
        System.out.println("    1 2 3 4 5");
        System.out.println("R 1|.|.|.|.|.|");
        System.out.println("O 2|.|.|.|.|.|");
        System.out.println("W 3|.|.|.|.|.|");
        System.out.println();
    }

    public Cell getTurn() {
        return turn;
    }

    @Override
    public Position getPosition() {
        return POSITION;
    }

    @Override
    public GameResult makeMove(Move move) {
        moveCounter++;

        if (!isValid(move)) {
            return GameResult.LOOSE;
        }

        field[move.getRow()][move.getCol()] = move.getValue();
        if (checkWin(move)) {
            return GameResult.WIN;
        }

        if (checkDraw()) {
            return GameResult.DRAW;
        }

        turn = turn == Cell.X ? Cell.O : Cell.X;
        return GameResult.UNKNOWN;
    }

    private boolean checkDraw() {
        return moveCounter == BOARD_AREA;
    }

    private boolean checkWin(Move move) {
        int row = move.getRow();
        int column = move.getCol();
        int maxInRow = 0;

        // horizontal check
        int leftBorder = Math.max(0, column - WIN_ROW + 1);
        int rightBorder = Math.min(FIELD_WIDTH - 1, column + WIN_ROW - 1);
        int counter = 0;
        for (int i = leftBorder; i <= rightBorder; i++) {
            if (field[row][i] == turn) {
                counter++;
            } else {
                maxInRow = Math.max(maxInRow, counter);
                counter = 0;
            }
        }
        if (Math.max(counter, maxInRow) >= WIN_ROW) {
            return true;
        }

        // vertical check
        int topBorder = Math.max(0, row - WIN_ROW + 1);
        int bottomBorder = Math.min(FIELD_HEIGHT - 1, row + WIN_ROW - 1);
        counter = 0;
        for (int i = topBorder; i <= bottomBorder; i++) {
            if (field[i][column] == turn) {
                counter++;
            } else {
                maxInRow = Math.max(maxInRow, counter);
                counter = 0;
            }
        }
        if (Math.max(counter, maxInRow) >= WIN_ROW) {
            return true;
        }

        // diagonal check
        int leftUp = Math.min(column - leftBorder, row - topBorder);
        int rightDown = Math.min(rightBorder - column, bottomBorder - row);
        counter = 0;
        for (int i = -leftUp; i <= rightDown; i++) {
            if (field[row + i][column + i] == turn) {
                counter++;
            } else {
                maxInRow = Math.max(maxInRow, counter);
                counter = 0;
            }
        }
        if (Math.max(counter, maxInRow) >= WIN_ROW) {
            return true;
        }
        int rightUp = Math.min(rightBorder - column, row - topBorder);
        int leftDown = Math.min(column - leftBorder, bottomBorder - row);
        counter = 0;
        for (int i = -rightUp; i <= leftDown; i++) {
            if (field[row + i][column - i] == turn) {
                counter++;
            } else {
                maxInRow = Math.max(maxInRow, counter);
                counter = 0;
            }
        }
        if (Math.max(counter, maxInRow) >= WIN_ROW) {
            return true;
        }

        return false;
    }

    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < FIELD_HEIGHT
                && 0 <= move.getCol() && move.getCol() < FIELD_WIDTH
                && field[move.getRow()][move.getCol()] == Cell.E
                && turn == move.getValue();
    }

    @Override
    public void clear() {
        for (Cell[] row : field) {
            Arrays.fill(row, Cell.E);
        }
        moveCounter = 0;
        turn = Cell.X;
    }

    @Override
    public String toString() {
        return POSITION.toString();
    }
}
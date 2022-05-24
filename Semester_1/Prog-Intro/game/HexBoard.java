package game;

import java.util.Arrays;

public class HexBoard implements Board {
    private final Cell[] field;
    private final int FIELD_HEIGHT;
    private final int BOARD_AREA;
    private final int WIN_ROW;
    private Cell turn;
    private final HexPosition POSITION;

    private int moveCounter;

    public HexBoard(int m, int k) {
        printRules();
        FIELD_HEIGHT = m;
        BOARD_AREA = m * m;
        field = new Cell[BOARD_AREA];
        POSITION = new HexPosition(this, m, field);
        WIN_ROW = k;
        clear();
    }

    @Override
    public void printRules() {
        System.out.println("In TicTacToe on hex board moves must be entered as 2 Integer numbers r and c, ");
        System.out.println("which define the hex at the intersection of row r and column c: ");
        System.out.println("      COLUMN");
        System.out.println("     1   2   3");
        System.out.println("    / \\ / \\ / \\");
        System.out.println("R 1| . | . | . |");
        System.out.println("    \\ / \\ / \\ / \\");
        System.out.println("  O 2| . | . | . |");
        System.out.println("      \\ / \\ / \\ / \\");
        System.out.println("    W 3| . | . | . |");
        System.out.println("        \\ / \\ / \\ /");
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

        field[move.getRow() * FIELD_HEIGHT + move.getCol()] = move.getValue();
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
        int rightBorder = Math.min(FIELD_HEIGHT - 1, column + WIN_ROW - 1);
        int counter = 0;
        for (int i = leftBorder; i <= rightBorder; i++) {
            if (field[row * FIELD_HEIGHT + i] == turn) {
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
            if (field[i * FIELD_HEIGHT + column] == turn) {
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
        int diagonalMax = Math.min(row - topBorder, rightBorder - column);
//        diagonalMax = Math.min(diagonalMax, WIN_ROW);
        int diagonalMin = Math.min(bottomBorder - row, column - leftBorder);
//        diagonalMin = Math.min(diagonalMin, WIN_ROW);
        counter = 0;
        for (int i = -diagonalMin; i <= diagonalMax; i++) {
            if (field[(row - i) * FIELD_HEIGHT + column + i] == turn) {
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
                && 0 <= move.getCol() && move.getCol() < FIELD_HEIGHT
                && POSITION.getCell(move.getRow(), move.getCol()) == Cell.E
                && turn == move.getValue();
    }

    @Override
    public void clear() {
        Arrays.fill(field, Cell.E);
        moveCounter = 0;
        turn = Cell.X;
    }

    @Override
    public String toString() {
        return POSITION.toString();
    }
}

package game;

public class SequentialPlayer implements Player {
    private final int M;
    private final int N;

    public SequentialPlayer(int m, int n) {
        M = m;
        N = n;
    }

    @Override
    public Move makeMove(Position position) {
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                final Move move = new Move(r, c, position.getTurn());
                if (position.isValid(move)) {
                    return move;
                }
            }
        }
        throw new AssertionError("No valid moves");
    }
}

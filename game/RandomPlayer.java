package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private final Random random = new Random();
    private final int M;
    private final int N;

    public RandomPlayer(int m, int n) {
        this.M = m;
        this.N = n;
    }

    @Override
    public Move makeMove(Position position) {
        while (true) {
            final Move move = new Move(
                    random.nextInt(M),
                    random.nextInt(N),
                    position.getTurn()
            );
            if (position.isValid(move)) {
                return move;
            }
        }
    }
}

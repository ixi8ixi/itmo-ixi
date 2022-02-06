package game;

import java.util.List;

public class Tournament {
    private final Board board;
    private final int playersNum;
    private final List<Player> players;
    private final TournamentTable table;
    private int roundCounter;

    public Tournament(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
        playersNum = players.size();
        table = new TournamentTable(playersNum);
        roundCounter = 0;
    }

    public void play(boolean log) {
        for (int i = 0; i < playersNum; i++) {
            for (int j = 0; j < playersNum; j++) {
                if (i == j) {
                    continue;
                }
                roundCounter++;
                System.out.printf("ROUND %d: PLAYER %d  VS  PLAYER %d%n", roundCounter, i + 1, j + 1);
                System.out.printf("Player number %d makes the first move%n", i + 1);
                System.out.printf("Good luck!%n");
                board.clear();
                int tourResult = new TwoPlayerGame(board, players.get(i), players.get(j)).play(log);
                switch (tourResult) {
                    case 1:
                        table.addResult(i, j, GameResult.WIN);
                        System.out.printf("Player %d won the round!%n%n", i + 1);
                        break;
                    case 2:
                        table.addResult(i, j, GameResult.LOOSE);
                        System.out.printf("Player %d won the round!%n%n", j + 1);
                        break;
                    case 0:
                        table.addResult(i, j, GameResult.DRAW);
                        System.out.printf("Draw!%n%n");
                        break;
                    default:
                        throw new AssertionError("Unknown result " + tourResult);
                }
            }
        }
        System.out.println("RESULTS: ");
        System.out.println(table);
        table.drawFinishSum();
    }
}

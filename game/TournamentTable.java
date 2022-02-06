package game;

public class TournamentTable {
    private final int[][] pointTable;
    private final int PLAYERS_NUM;
    private int cellLength;
    private int maxInTable;

    public TournamentTable(int playersNum) {
        this.PLAYERS_NUM = playersNum;
        cellLength = String.valueOf(PLAYERS_NUM).length();
        this.pointTable = new int[playersNum][playersNum];
    }

    public void addResult(int player1, int player2, GameResult result) {
        switch (result) {
            case WIN:
                pointTable[player1][player2] += 3;
                break;
            case DRAW:
                pointTable[player1][player2] += 1;
                pointTable[player2][player1] += 1;
                break;
            case LOOSE:
                pointTable[player2][player1] += 3;
                break;
            default:
                throw new IllegalStateException("Result of the tour: " + result);
        }
        maxInTable = Math.max(maxInTable, pointTable[player1][player2]);
        maxInTable = Math.max(maxInTable, pointTable[player2][player1]);
    }

    public String drawCell(String string, int len) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len - string.length(); i++) {
            result.append(" ");
        }
        result.append(string);
        return result.toString();
    }

    public void drawFinishSum() {
        System.out.print(System.lineSeparator());
        StringBuilder playerInfo = new StringBuilder();
        for (int i = 0; i < PLAYERS_NUM; i++) {
            playerInfo.delete(0, playerInfo.length());
            playerInfo.append(String.format("Player %d", i + 1));
            int pointSum = 0;
            for (int j = 0; j < PLAYERS_NUM; j++) {
                if (i == j) {
                    continue;
                }
                pointSum += pointTable[i][j];
            }
            playerInfo.append(" .......... ");
            playerInfo.append(pointSum);
            System.out.println(playerInfo);
        }
    }

    @Override
    public String toString() {
        cellLength = String.valueOf(maxInTable).length();
        final StringBuilder sb = new StringBuilder(drawCell("", cellLength));
        sb.append("|");
        for (int i = 0; i < PLAYERS_NUM; i++) {
            sb.append(drawCell(String.valueOf(i + 1), cellLength));
            sb.append("|");
        }
        for (int i = 0; i < PLAYERS_NUM; i++) {
            sb.append(System.lineSeparator());
            sb.append(drawCell(String.valueOf(i + 1), cellLength));
            sb.append("|");
            for (int j = 0; j < PLAYERS_NUM; j++) {
                if (i == j) {
                    sb.append(drawCell("X", cellLength));
                } else {
                    sb.append(drawCell(String.valueOf(pointTable[i][j]), cellLength));
                }
                sb.append("|");
            }
        }
        return sb.toString();
    }
}

import java.util.Random;

public class MyGraph {
    private final int size;
    private final boolean[][] table;
    private final Random random = new Random();

    public MyGraph(int size) {
        this.size = size;
        table = new boolean[size][size];
    }

    private boolean isConnected(int from, int to) {
        return table[from][to];
    }

    private void addEdge(int from, int to) {
        table[from][to] = table[to][from] = true;
    }

    private void clearTable() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                table[i][j] = false;
            }
        }
    }

    public void generate(double c) {
        double border = c / size;
        clearTable();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < i; j++) {
                if (random.nextDouble() <= border) {
                    addEdge(i, j);
                }
            }
        }
    }

    public long countTriangles() {
        long result = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < i; j++) {
                for (int k = 0; k < j; k++) {
                    if (isConnected(i, j) && isConnected(j, k) && isConnected(i, k)) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public boolean containsTriangle() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < i; j++) {
                for (int k = 0; k < j; k++) {
                    if (isConnected(i, j) && isConnected(j, k) && isConnected(i, k)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(table[i][j] ? "1" : "0").append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

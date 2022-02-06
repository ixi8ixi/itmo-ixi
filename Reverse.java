import java.io.IOException;
import java.util.Arrays;

public class Reverse {
    public static void main(String[] args) {
        int[][] ints = new int[1][1];
        try {
            int row = 0;
            MyScanner liner = new MyScanner(System.in);
            try {
                while (liner.hasNextLine()) {
                    MyScanner inter = new MyScanner(liner.nextLine());
                    try {
                        int column = 0;
                        if (row == ints.length) {
                            ints = Arrays.copyOf(ints, ints.length * 2);
                        }
                        ints[row] = new int[1];
                        while (inter.hasNext()) {
                            if (column == ints[row].length) {
                                ints[row] = Arrays.copyOf(ints[row], ints[row].length * 2);
                            }
                            ints[row][column++] = inter.nextInt();
                        }
                        ints[row] = Arrays.copyOf(ints[row], column);
                        row++;
                    } finally {
                        inter.close();
                    }
                }
                ints = Arrays.copyOf(ints, row);
            } finally {
                liner.close();
            }
        } catch (IOException e) {
            System.out.println("Cannot read input: " + e.getMessage());
        }

        for (int j = ints.length - 1; j >= 0 ; j --) {
            for (int i = ints[j].length - 1; i >= 0 ; i --) {
                System.out.print(ints[j][i] + " ");
            }
            System.out.println();
        }
    }
}

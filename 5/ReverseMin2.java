import java.io.IOException;
import java.util.Arrays;

public class ReverseMin2 {
    public static void main(String[] args) {
        int[][] ints = new int[1][1];
        int row = 0;
        try {
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
            System.out.println("Cannot read input file: " + e.getMessage());
        }


        for (int j = 0; j < ints.length; j ++) {
            int check = j - 1;
            for (int i = 0; i < ints[j].length; i ++) {
                // Upper check
                while (check >= 0 && i + 1 > ints[check].length) {
                    check --;
                }
                if (check >= 0) {
                    ints[j][i] = min(ints[j][i], ints[check][i]);
                }
                // Left check
                if (i > 0) {
                    ints[j][i] = min(ints[j][i], ints[j][i - 1]);
                }
            }
        }

        for (int j = 0; j < ints.length; j ++) {
            for (int i = 0; i < ints[j].length; i ++) {
                System.out.print(ints[j][i] + " ");
            }
            System.out.println();
        }

    }

    public static int min(int a, int b) {
        if (a <= b) {
            return a;
        } else {
            return b;
        }
    }
}
package game;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final int result = new TwoPlayerGame(
                new HexBoard(3, 5),
                //new SequentialPlayer(11, 11),
                new RandomPlayer (3, 3),
                new HumanPlayer(new Scanner(System.in))
        ).play(false);
        switch (result) {
            case 1:
                System.out.println("First player won");
                break;
            case 2:
                System.out.println("Second player won");
                break;
            case 0:
                System.out.println("Draw");
                break;
            default:
                throw new AssertionError("Unknown result " + result);
        }
//        new Tournament(new GexBoard(11, 3), List.of(
//                new HumanPlayer(new Scanner(System.in)),
//                new HumanPlayer(new Scanner(System.in)),
//                new RandomPlayer(11, 11)
//        )).play(false);
    }
}

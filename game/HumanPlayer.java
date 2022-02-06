package game;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private Scanner in;

    public HumanPlayer(Scanner in) {
        this.in = in;
    }

    private boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Move makeMove(Position position) {
        System.out.println();
        System.out.println("Current position");
        System.out.println(position);
        System.out.println("Enter you move for " + position.getTurn());
        while (true) {
            try {
                String row = in.next();
                String column = in.next();
                in.nextLine();
                if (isInteger(row) && isInteger(column)) {
                    return new Move(Integer.parseInt(row) - 1,
                            Integer.parseInt(column) - 1,
                            position.getTurn());
                } else {
                    System.out.println("Incorrect move format, please try again: ");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Incorrect move format, please try again: ");
                in = new Scanner(System.in);
            }
        }
    }
}

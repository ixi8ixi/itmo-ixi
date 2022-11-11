import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TimeTest {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new FileInputStream("input.txt"));
        for (int i = 0; i < 4000; i++) {
            in.nextLine();
        }
    }
}

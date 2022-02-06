import java.util.Arrays;

public class IntList {
    private int amount;
    private int realAmount;
    private int[] numbers;
    private boolean isEven;

    public IntList() {
        amount = 0;
        realAmount = 0;
        numbers = new int[1];
        isEven = true;
    }

    public void addEntry(int newEntry) {
        amount++;
        realAmount++;
        if (realAmount > numbers.length) {
            numbers = Arrays.copyOf(numbers, numbers.length * 2);
        }
        numbers[realAmount - 1] = newEntry;
    }

    public void cutEnd() {
        numbers = Arrays.copyOf(numbers, realAmount);
    }

    public void increaseAmount() {
        amount++;
    }

    public void trueParity() {
        isEven = true;
    }

    public int getAmount() {
        return amount;
    }

    public int[] getEnt() {
        return numbers;
    }

    public boolean getParity() {
        return isEven;
    }

    public void changeParity() {
        isEven = !isEven;
    }
}

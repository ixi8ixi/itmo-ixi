import java.util.Arrays;

public class IntList {
    private int amount;
    private int realAmount;
    private boolean inStringParity = true;
    private int[] entryNumbers;

    public IntList(int firstEntryNumber) {
        this.amount = 1;
        this.realAmount = 0;
        this.entryNumbers = new int[]{firstEntryNumber};
    }

    public int getAmount() {
        return amount;
    }

    public int[] getEntryNumbers() {
        return entryNumbers;
    }

    public boolean getParity() {
        return inStringParity;
    }

    public void falseParity() {
        this.inStringParity = false;
    }

    public void addEntryNumber(int entryNumber) {
        if (inStringParity) {
            realAmount++;
            if (realAmount > entryNumbers.length) {
                this.entryNumbers = Arrays.copyOf(entryNumbers, entryNumbers.length * 2 + 1);
            }
            entryNumbers[realAmount - 1] = entryNumber;
        }
        this.inStringParity = !this.inStringParity;
        this.amount += 1;
    }

    public void cutEnd() {
        this.entryNumbers = Arrays.copyOf(entryNumbers, realAmount);
    }

    @Override
    public String toString() {
        return "Amount: " + this.amount + " EntryNumbers: " + Arrays.toString(this.entryNumbers);
    }
}

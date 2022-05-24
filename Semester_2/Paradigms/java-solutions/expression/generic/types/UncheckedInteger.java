package expression.generic.types;

public class UncheckedInteger extends AbstractType<Integer> {
    public UncheckedInteger() {
    }

    public UncheckedInteger(int value) {
        setIntValue(value);
    }

    @Override
    public void setIntValue(int value) {
        this.value = value;
    }

    @Override
    public UncheckedInteger makeNegate() {
        return new UncheckedInteger(value * (-1));
    }

    @Override
    public UncheckedInteger makeSum(AbstractType<Integer> arg) {
        return new UncheckedInteger(getValue() + arg.getValue());
    }

    @Override
    public UncheckedInteger makeSubtract(AbstractType<Integer> arg) {
        return new UncheckedInteger(getValue() - arg.getValue());
    }

    @Override
    public UncheckedInteger makeMultiply(AbstractType<Integer> arg) {
        return new UncheckedInteger(getValue() * arg.getValue());
    }

    @Override
    public UncheckedInteger makeDivide(AbstractType<Integer> arg) {
        return new UncheckedInteger(getValue() / arg.getValue());
    }

    @Override
    public UncheckedInteger makeMin(AbstractType<Integer> arg) {
        return new UncheckedInteger(Integer.min(getValue(), arg.getValue()));
    }

    @Override
    public UncheckedInteger makeMax(AbstractType<Integer> arg) {
        return new UncheckedInteger(Integer.max(getValue(), arg.getValue()));
    }

    @Override
    public UncheckedInteger makeCount() {
        return new UncheckedInteger(Integer.bitCount(value));
    }
}
package expression.generic.types;

public class MyLong extends AbstractType<Long> {
    public MyLong() {
    }

    public MyLong(int value) {
        this.value = (long) value;
    }

    public MyLong(Long value) {
        this.value = value;
    }

    @Override
    public void setIntValue(int value) {
        this.value = (long) value;
    }

    @Override
    public MyLong makeNegate() {
        return new MyLong(value * (-1));
    }

    @Override
    public MyLong makeSum(AbstractType<Long> arg) {
        return new MyLong(getValue() + arg.getValue());
    }

    @Override
    public MyLong makeSubtract(AbstractType<Long> arg) {
        return new MyLong(getValue() - arg.getValue());
    }

    @Override
    public MyLong makeMultiply(AbstractType<Long> arg) {
        return new MyLong(getValue() * arg.getValue());
    }

    @Override
    public MyLong makeDivide(AbstractType<Long> arg) {
        return new MyLong(getValue() / arg.getValue());
    }

    @Override
    public MyLong makeMin(AbstractType<Long> arg) {
        return new MyLong(getValue().compareTo(arg.getValue()) <= 0 ? getValue() : arg.getValue());
    }

    @Override
    public MyLong makeMax(AbstractType<Long> arg) {
        return new MyLong(getValue().compareTo(arg.getValue()) >= 0 ? getValue() : arg.getValue());
    }

    @Override
    public MyLong makeCount() {
        return new MyLong(Long.bitCount(value));
    }
}

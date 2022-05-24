package expression.generic.types;

public class MyDouble extends AbstractType<Double> {
    public MyDouble() {
    }

    public MyDouble(int value) {
        this.value = (double) value;
    }

    public MyDouble(double value) {
        this.value = value;
    }

    @Override
    public void setIntValue(int value) {
        this.value = (double) value;
    }

    @Override
    public MyDouble makeNegate() {
        return new MyDouble(value * (-1));
    }

    @Override
    public MyDouble makeSum(AbstractType<Double> arg) {
        return new MyDouble(getValue() + arg.getValue());
    }

    @Override
    public MyDouble makeSubtract(AbstractType<Double> arg) {
        return new MyDouble(getValue() - arg.getValue());
    }

    @Override
    public MyDouble makeMultiply(AbstractType<Double> arg) {
        return new MyDouble(getValue() * arg.getValue());
    }

    @Override
    public MyDouble makeDivide(AbstractType<Double> arg) {
        return new MyDouble(getValue() / arg.getValue());
    }

    @Override
    public MyDouble makeMin(AbstractType<Double> arg) {
        return new MyDouble(Double.min(getValue(), arg.getValue()));
    }

    @Override
    public MyDouble makeMax(AbstractType<Double> arg) {
        return new MyDouble(Double.max(getValue(), arg.getValue()));
    }

    @Override
    public AbstractType<Double> makeCount() {
        return new MyDouble(Long.bitCount(Double.doubleToLongBits(value)));
    }
}

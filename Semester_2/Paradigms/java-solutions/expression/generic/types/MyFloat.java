package expression.generic.types;

public class MyFloat extends AbstractType<Float> {
    public MyFloat() {
    }

    public MyFloat(int value) {
        this.value = (float) value;
    }

    public MyFloat(Float value) {
        this.value = value;
    }

    @Override
    public void setIntValue(int value) {
        this.value = (float) value;
    }

    @Override
    public MyFloat makeNegate() {
        return new MyFloat(value * (-1));
    }

    @Override
    public MyFloat makeSum(AbstractType<Float> arg) {
        return new MyFloat(getValue() + arg.getValue());
    }

    @Override
    public MyFloat makeSubtract(AbstractType<Float> arg) {
        return new MyFloat(getValue() - arg.getValue());
    }

    @Override
    public MyFloat makeMultiply(AbstractType<Float> arg) {
        return new MyFloat(getValue() * arg.getValue());
    }

    @Override
    public MyFloat makeDivide(AbstractType<Float> arg) {
        return new MyFloat(getValue() / arg.getValue());
    }

    @Override
    public MyFloat makeMin(AbstractType<Float> arg) {
        return new MyFloat(Float.min(getValue(), arg.getValue()));
    }

    @Override
    public MyFloat makeMax(AbstractType<Float> arg) {
        return new MyFloat(Float.max(getValue(), arg.getValue()));
    }

    @Override
    public MyFloat makeCount() {
        return new MyFloat(Integer.bitCount(Float.floatToIntBits(value)));
    }
}

package expression.generic.types;

public abstract class AbstractType<T> {
    protected T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract void setIntValue(int value);

    public abstract AbstractType<T> makeNegate();
    public abstract AbstractType<T> makeSum(AbstractType<T> arg);
    public abstract AbstractType<T> makeSubtract(AbstractType<T> arg);
    public abstract AbstractType<T> makeMultiply(AbstractType<T> arg);
    public abstract AbstractType<T> makeDivide(AbstractType<T> arg);
    public abstract AbstractType<T> makeMin(AbstractType<T> arg);
    public abstract AbstractType<T> makeMax(AbstractType<T> arg);
    public abstract AbstractType<T> makeCount();

    @Override
    public String toString() {
        return value.toString();
    }
}

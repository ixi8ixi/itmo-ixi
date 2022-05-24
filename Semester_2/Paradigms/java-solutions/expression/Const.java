package expression;

import expression.generic.types.AbstractType;

import java.util.Objects;
import java.util.function.Supplier;

public class Const<N> implements TripleExpression<N> {
    private final AbstractType<N> value;

    public Const(int arg, Supplier<AbstractType<N>> valueInstanceCreator) {
        this.value = valueInstanceCreator.get();
        this.value.setIntValue(arg);
    }

    @Override
    public AbstractType<N> evaluate(int x, int y, int z) {
        return value;
    }

    @Override
    public String toMiniString() {
        return value.toString();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getRightInOperationPriority() {
        return 0;
    }

    @Override
    public int getLeftInOperationPriority() {
        return 0;
    }

    @Override
    public String getOperationString() {
        throw new AssertionError();
    }

    @Override
    public void makeString(StringBuilder in) {
        in.append(value);
    }

    @Override
    public void makeMiniString(StringBuilder in) {
        in.append(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Const<?> aConst = (Const<?>) o;
        return Objects.equals(value, aConst.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

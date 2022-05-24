package expression;

import expression.generic.types.AbstractType;

import java.util.Objects;

public abstract class AbstractUnaryOperation<N> implements TripleExpression<N> {
    private final TripleExpression<N> operand;

    public AbstractUnaryOperation(TripleExpression<N> operand) {
        this.operand = operand;
    }

    public abstract String getOperationString();
    public abstract String getMiniOperationString();
    public abstract AbstractType<N> makeOperation(AbstractType<N> a);
    public abstract int getPriority();
    public abstract int getRightInOperationPriority();

    @Override
    public AbstractType<N> evaluate(int x, int y, int z) {
        return makeOperation(operand.evaluate(x, y, z));
    }

    @Override
    public void makeString(StringBuilder in) {
        in.append(getOperationString());
        in.append('(');
        operand.makeString(in);
        in.append(')');
    }

    @Override
    public String toString() {
        StringBuilder in = new StringBuilder();
        makeString(in);
        return in.toString();
    }

    @Override
    public void makeMiniString(StringBuilder in) {
        if (operand.getPriority() > 500) {
            in.append(getOperationString()).append('(');
            operand.makeMiniString(in);
            in.append(')');
        } else {
            in.append(getMiniOperationString());
            operand.makeMiniString(in);
        }
    }

    @Override
    public String toMiniString() {
        StringBuilder in = new StringBuilder();
        makeMiniString(in);
        return in.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUnaryOperation<?> that = (AbstractUnaryOperation<?>) o;
        return Objects.equals(operand, that.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }
}

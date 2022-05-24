package expression;

import expression.generic.types.AbstractType;

import java.util.Objects;

public abstract class AbstractBinaryOperation<N> implements TripleExpression<N> {
    private final TripleExpression<N> leftOperand;
    private final TripleExpression<N> rightOperand;

    public AbstractBinaryOperation(final TripleExpression<N> leftOperand,
                                   final TripleExpression<N> rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public abstract String getOperationString();
    public abstract <T extends AbstractType<N>> AbstractType<N> makeOperation(T a, T b);
    public abstract int getPriority();
    public abstract int getRightInOperationPriority();
    public abstract int getLeftInOperationPriority();
    public abstract boolean isLeftAssociative();
    public abstract boolean isRightAssociative();

    @Override
    public AbstractType<N> evaluate(int x, int y, int z) {
        return makeOperation(leftOperand.evaluate(x, y, z), rightOperand.evaluate(x, y, z));
    }

    private void bracketWrapper(final StringBuilder in, final boolean left) {
        final TripleExpression<N> operand = left ?  leftOperand : rightOperand;

        if (getPriority() < operand.getPriority() ||
                (getPriority() == operand.getPriority() &&
                        (left && (!isLeftAssociative() ||
                                operand.getLeftInOperationPriority() < getLeftInOperationPriority()) ||
                                !left && (!isRightAssociative() ||
                                        operand.getRightInOperationPriority() < getRightInOperationPriority()))
                )
        ) {
            in.append("(");
            operand.makeMiniString(in);
            in.append(")");
        } else {
            operand.makeMiniString(in);
        }
    }

    @Override
    public void makeMiniString(final StringBuilder in) {
        bracketWrapper(in, true);
        in.append(getOperationString());
        bracketWrapper(in, false);
    }

    @Override
    public String toMiniString() {
        final StringBuilder in = new StringBuilder();
        makeMiniString(in);
        return in.toString();
    }

    @Override
    public void makeString(final StringBuilder in) {
        in.append("(");
        leftOperand.makeString(in);
        in.append(getOperationString());
        rightOperand.makeString(in);
        in.append(")");
    }

    @Override
    public String toString() {
        final StringBuilder in = new StringBuilder();
        makeString(in);
        return in.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBinaryOperation<?> that = (AbstractBinaryOperation<?>) o;
        return Objects.equals(leftOperand, that.leftOperand) && Objects.equals(rightOperand, that.rightOperand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperand, rightOperand);
    }
}

package expression;

import expression.generic.types.AbstractType;

public class Subtract<N> extends AbstractBinaryOperation<N> {
    public Subtract(final TripleExpression<N> leftOperand, TripleExpression<N> rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public String getOperationString() {
        return " - ";
    }

    @Override
    public <T extends AbstractType<N>> AbstractType<N> makeOperation(T a, T b) {
        return a.makeSubtract(b);
    }

    @Override
    public int getPriority() {
        return 2000;
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
    public boolean isLeftAssociative() {
        return true;
    }

    @Override
    public boolean isRightAssociative() {
        return false;
    }
}

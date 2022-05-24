package expression;

import expression.generic.types.AbstractType;

public class Multiply<N> extends AbstractBinaryOperation<N> {
    public Multiply(TripleExpression<N> leftOperand, TripleExpression<N> rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public String getOperationString() {
        return " * ";
    }

    @Override
    public <T extends AbstractType<N>> AbstractType<N> makeOperation(T a, T b) {
        return a.makeMultiply(b);
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public int getRightInOperationPriority() {
        return 1;
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
        return true;
    }
}

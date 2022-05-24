package expression;

import expression.generic.types.AbstractType;

public class Divide<N> extends AbstractBinaryOperation<N> {
    public Divide(TripleExpression<N> leftOperand, TripleExpression<N> rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public String getOperationString() {
        return " / ";
    }

    @Override
    public <T extends AbstractType<N>> AbstractType<N> makeOperation(T a, T b) {
        return a.makeDivide(b);
    }

    @Override
    public int getPriority() {
        return 1000;
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

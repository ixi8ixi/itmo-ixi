package expression;

import expression.generic.types.AbstractType;

public class MaxOperation<N> extends AbstractBinaryOperation<N> {
    public MaxOperation(TripleExpression<N> leftOperand, TripleExpression<N> rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public String getOperationString() {
        return " max ";
    }

    @Override
    public <T extends AbstractType<N>> AbstractType<N> makeOperation(T a, T b) {
        return a.makeMax(b);
    }

    @Override
    public int getPriority() {
        return 3000;
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
        return true;
    }
}

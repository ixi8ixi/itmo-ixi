package expression;

import expression.generic.types.AbstractType;

public class MinOperation<N> extends AbstractBinaryOperation<N> {
    public MinOperation(TripleExpression<N> leftOperand, TripleExpression<N> rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public String getOperationString() {
        return " min ";
    }

    @Override
    public <T extends AbstractType<N>> AbstractType<N> makeOperation(T a, T b) {
        return a.makeMin(b);
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

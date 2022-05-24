package expression;

import expression.generic.types.AbstractType;

public class Negate<N> extends AbstractUnaryOperation<N> {
    public Negate(TripleExpression<N> operand) {
        super(operand);
    }

    @Override
    public String getOperationString() {
        return "-";
    }

    @Override
    public String getMiniOperationString() {
        return  "- ";
    }

    @Override
    public AbstractType<N> makeOperation(AbstractType<N> a) {
        return a.makeNegate();
    }

    @Override
    public int getPriority() {
        return 500;
    }

    @Override
    public int getRightInOperationPriority() {
        return 0;
    }

    @Override
    public int getLeftInOperationPriority() {
        return 0;
    }
}

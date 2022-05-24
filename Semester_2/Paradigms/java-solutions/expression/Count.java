package expression;

import expression.generic.types.AbstractType;

public class Count<N> extends AbstractUnaryOperation<N> {
    public Count(TripleExpression<N> operand) {
        super(operand);
    }

    @Override
    public String getOperationString() {
        return "count";
    }

    @Override
    public String getMiniOperationString() {
        return "count ";
    }

    @Override
    public AbstractType<N> makeOperation(AbstractType<N> a) {
        return a.makeCount();
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
}

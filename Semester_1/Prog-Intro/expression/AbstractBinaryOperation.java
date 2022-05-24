package expression;

import java.math.BigInteger;

public abstract class AbstractBinaryOperation implements UnitedExpression {
    private final UnitedExpression FIRST_OPERAND;
    private final UnitedExpression SECOND_OPERAND;
    private final int PRIORITY;
    private final boolean IS_RIGHT_ASSOCIATIVE;
    private final boolean IS_LEFT_ASSOCIATIVE;

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    public AbstractBinaryOperation(UnitedExpression firstOperand,
                                   UnitedExpression secondOperand,
                                   int priority,
                                   boolean isLeftAssociative,
                                   boolean isRightAssociative) {
        this.FIRST_OPERAND = firstOperand;
        this.SECOND_OPERAND = secondOperand;
        this.PRIORITY = priority;
        this.IS_RIGHT_ASSOCIATIVE = isRightAssociative;
        this.IS_LEFT_ASSOCIATIVE = isLeftAssociative;
    }

    abstract int makeOperation(int firstOperand, int secondOperand);
    abstract BigInteger makeOperation(BigInteger firstOperand, BigInteger secondOperand);
    public abstract String getOperationString();

    public UnitedExpression getFirstOperand() {
        return FIRST_OPERAND;
    }

    public UnitedExpression getSecondOperand() {
        return SECOND_OPERAND;
    }

    @Override
    public int evaluate(int x) {
        return makeOperation(FIRST_OPERAND.evaluate(x), SECOND_OPERAND.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return makeOperation(FIRST_OPERAND.evaluate(x, y, z), SECOND_OPERAND.evaluate(x, y, z));
    }

    @Override
    public BigInteger evaluate(BigInteger x) {
        return makeOperation(FIRST_OPERAND.evaluate(x), SECOND_OPERAND.evaluate(x));
    }

    @Override
    public String toString() {
        return '(' +
                FIRST_OPERAND.toString() +
                getOperationString() +
                SECOND_OPERAND.toString() +
                ')';
    }

    @Override
    public String toMiniString() {
        StringBuilder sb = new StringBuilder();
        if (PRIORITY < FIRST_OPERAND.getPriority() ||
                (!IS_LEFT_ASSOCIATIVE && FIRST_OPERAND.getPriority() > 0)) {
            sb.append('(').append(FIRST_OPERAND.toMiniString()).append(')');
        } else {
            sb.append(FIRST_OPERAND.toMiniString());
        }
        sb.append(getOperationString());
        if (PRIORITY < SECOND_OPERAND.getPriority() ||
                (!IS_RIGHT_ASSOCIATIVE && SECOND_OPERAND.getPriority() > 0 && getPriority() == SECOND_OPERAND.getPriority()) ||
                (getOperationString().equals(" * ") && SECOND_OPERAND.getOperationString().equals(" / "))) {
            sb.append('(').append(SECOND_OPERAND.toMiniString()).append(')');
        } else {
            sb.append(SECOND_OPERAND.toMiniString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AbstractBinaryOperation) {
            AbstractBinaryOperation check = (AbstractBinaryOperation) object;
            return getOperationString().equals(check.getOperationString()) &&
                    FIRST_OPERAND.equals(check.getFirstOperand()) &&
                    SECOND_OPERAND.equals(check.getSecondOperand());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((FIRST_OPERAND.hashCode() * 17) + SECOND_OPERAND.hashCode()) * 17 + getOperationString().hashCode();
    }
}

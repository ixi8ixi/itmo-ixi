package expression;

import java.math.BigInteger;

public class Divide extends AbstractBinaryOperation {
    public Divide(UnitedExpression firstOperand, UnitedExpression secondOperand) {
        super(firstOperand, secondOperand, 1000, true, false);
    }

    @Override
    int makeOperation(int firstOperand, int secondOperand) {
        return firstOperand / secondOperand;
    }

    @Override
    BigInteger makeOperation(BigInteger firstOperand, BigInteger secondOperand) {
        return firstOperand.divide(secondOperand);
    }

    @Override
    public String getOperationString() {
        return " / ";
    }
}
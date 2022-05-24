package expression;

import java.math.BigInteger;

public class Multiply extends AbstractBinaryOperation {
    public Multiply(UnitedExpression firstOperand, UnitedExpression secondOperand) {
        super(firstOperand, secondOperand, 1000, true, true);
    }

    @Override
    int makeOperation(int firstOperand, int secondOperand) {
        return firstOperand * secondOperand;
    }

    @Override
    BigInteger makeOperation(BigInteger firstOperand, BigInteger secondOperand) {
        return firstOperand.multiply(secondOperand);
    }

    @Override
    public String getOperationString() {
        return " * ";
    }
}
package expression;

import java.math.BigInteger;

public class Subtract extends AbstractBinaryOperation {
    public Subtract(UnitedExpression firstOperand, UnitedExpression secondOperand) {
        super(firstOperand, secondOperand, 2000, true, false);
    }

    @Override
    int makeOperation(int firstOperand, int secondOperand) {
        return firstOperand - secondOperand;
    }

    @Override
    BigInteger makeOperation(BigInteger firstOperand, BigInteger secondOperand) {
        return firstOperand.subtract(secondOperand);
    }

    @Override
    public String getOperationString() {
        return " - ";
    }
}

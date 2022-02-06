package expression;

import java.math.BigInteger;

public class Add extends AbstractBinaryOperation {
    public Add(UnitedExpression firstOperand, UnitedExpression secondOperand) {
        super(firstOperand, secondOperand, 2000, true, true);
    }

    @Override
    int makeOperation(int firstOperand, int secondOperand) {
        return firstOperand + secondOperand;
    }

    @Override
    BigInteger makeOperation(BigInteger firstOperand, BigInteger secondOperand) {
        return firstOperand.add(secondOperand);
    }

    @Override
    public String getOperationString() {
        return " + ";
    }
}

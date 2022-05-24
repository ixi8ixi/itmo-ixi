package expression.generic.types;

import java.math.BigInteger;

public class MyBigInteger extends AbstractType<BigInteger> {
    public MyBigInteger() {
    }

    public MyBigInteger(int value) {
        this.value = BigInteger.valueOf(value);
    }

    public MyBigInteger(BigInteger value) {
        this.value = value;
    }

    @Override
    public void setIntValue(int value) {
        this.value = BigInteger.valueOf(value);
    }

    @Override
    public MyBigInteger makeNegate() {
        return new MyBigInteger(getValue().negate());
    }

    @Override
    public MyBigInteger makeSum(AbstractType<BigInteger> arg) {
        return new MyBigInteger(getValue().add(arg.getValue()));
    }

    @Override
    public MyBigInteger makeSubtract(AbstractType<BigInteger> arg) {
        return new MyBigInteger(getValue().subtract(arg.getValue()));
    }

    @Override
    public MyBigInteger makeMultiply(AbstractType<BigInteger> arg) {
        return new MyBigInteger(getValue().multiply(arg.getValue()));
    }

    @Override
    public MyBigInteger makeDivide(AbstractType<BigInteger> arg) {
        return new MyBigInteger(getValue().divide(arg.getValue()));
    }

    @Override
    public MyBigInteger makeMin(AbstractType<BigInteger> arg) {
        return new MyBigInteger(getValue().min(arg.getValue()));
    }

    @Override
    public MyBigInteger makeMax(AbstractType<BigInteger> arg) {
        return new MyBigInteger(getValue().max(arg.getValue()));
    }

    @Override
    public MyBigInteger makeCount() {
        return new MyBigInteger(value.bitCount());
    }
}

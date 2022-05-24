package expression;

import java.math.BigInteger;
import java.util.Objects;

public class Const implements UnitedExpression{
    private final BigInteger VALUE;

    @Override
    public int getPriority() {
        return 0;
    }

    public Const(int value) {
        VALUE = BigInteger.valueOf(value);
    }

    public Const(BigInteger value) {
        VALUE = value;
    }

    private BigInteger getValue() {
        return VALUE;
    }

    @Override
    public int evaluate(int x) {
        return VALUE.intValue();
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return VALUE.intValue();
    }

    @Override
    public BigInteger evaluate(BigInteger x) {
        return VALUE;
    }

    @Override
    public String toString() {
        return VALUE.toString();
    }

    @Override
    public String getOperationString() {
        return "num";
    }

    @Override
    public String toMiniString() {
        return VALUE.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Const) {
            return VALUE.equals(((Const) object).getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return VALUE.hashCode();
    }
}

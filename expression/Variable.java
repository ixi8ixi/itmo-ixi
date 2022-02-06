package expression;

import java.math.BigInteger;
import java.util.Objects;

public class Variable implements UnitedExpression {
    private final String VAR_NAME;

    @Override
    public int getPriority() {
        return 0;
    }

    public Variable(String varName) {
        this.VAR_NAME = varName;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (VAR_NAME) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
            default:
                throw new IllegalStateException("Illegal variable name: " + VAR_NAME);
        }
    }

    @Override
    public BigInteger evaluate(BigInteger x) {
        return x;
    }

    @Override
    public String toString() {
        return VAR_NAME;
    }

    @Override
    public String getOperationString() {
        return VAR_NAME;
    }

    @Override
    public String toMiniString() {
        return VAR_NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Variable) {
            return VAR_NAME.equals(((Variable) o).toMiniString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return VAR_NAME.hashCode();
    }
}

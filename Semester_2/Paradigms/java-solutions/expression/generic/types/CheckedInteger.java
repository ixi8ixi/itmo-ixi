package expression.generic.types;

public class CheckedInteger extends AbstractType<Integer> {
    public CheckedInteger() {
    }

    public CheckedInteger(int value) {
        setIntValue(value);
    }

    @Override
    public void setIntValue(int value) {
        this.value = value;
    }

    @Override
    public CheckedInteger makeNegate() {
        if (getValue().compareTo(Integer.MIN_VALUE) == 0) {
            throw new ArithmeticException("overflow");
        }
        
        return new CheckedInteger(value * (-1));
    }

    @Override
    public CheckedInteger makeSum(AbstractType<Integer> arg) {
        if (arg.getValue().compareTo(0) >= 0 && getValue().compareTo(Integer.MAX_VALUE - arg.getValue()) > 0) {
            throw new ArithmeticException("overflow");
        } else if (arg.getValue().compareTo(0) < 0 && getValue().compareTo(Integer.MIN_VALUE - arg.getValue()) < 0) {
            throw new ArithmeticException("overflow");
        } else if (arg.getValue().compareTo(Integer.MIN_VALUE) == 0 && getValue().compareTo(0) < 0) {
            throw new ArithmeticException("overflow");
        }

        return new CheckedInteger(getValue() + arg.getValue());
    }

    @Override
    public CheckedInteger makeSubtract(AbstractType<Integer> arg) {
        if (arg.getValue().compareTo(0) < 0 && getValue().compareTo(Integer.MAX_VALUE + arg.getValue()) > 0) {
            throw new ArithmeticException("overflow");
        } else if (arg.getValue().compareTo(0) >= 0 && getValue().compareTo(Integer.MIN_VALUE + arg.getValue()) < 0) {
            throw new ArithmeticException("overflow");
        }
        return new CheckedInteger(getValue() - arg.getValue());
    }

    @Override
    public CheckedInteger makeMultiply(AbstractType<Integer> arg) {
        if ((arg.getValue().compareTo(-1) == 0 && getValue().compareTo(Integer.MIN_VALUE) == 0) ||
                (arg.getValue().compareTo(Integer.MIN_VALUE) == 0 && getValue().compareTo(-1) == 0)) {
            throw new ArithmeticException("overflow");
        } else if (arg.getValue().compareTo(0) > 0) {
            if (getValue().compareTo(Integer.MAX_VALUE / arg.getValue()) > 0 ||
                    getValue().compareTo(Integer.MIN_VALUE / arg.getValue()) < 0) {
                throw new ArithmeticException("overflow");
            }
        } else if (arg.getValue().compareTo(-1) < 0) {
            if (getValue().compareTo(Integer.MAX_VALUE / arg.getValue()) < 0 ||
                    getValue().compareTo(Integer.MIN_VALUE / arg.getValue()) > 0) {
                throw new ArithmeticException("overflow");
            }
        }
        return new CheckedInteger(getValue() * arg.getValue());
    }

    @Override
    public CheckedInteger makeDivide(AbstractType<Integer> arg) {
        if (arg.getValue().compareTo(0) == 0) {
            throw new ArithmeticException("division by zero");
        }
        if (getValue().compareTo(Integer.MIN_VALUE) == 0 && arg.getValue().compareTo(-1) == 0) {
            throw new ArithmeticException("overflow");
        }
        return new CheckedInteger(getValue() / arg.getValue());
    }

    @Override
    public CheckedInteger makeMin(AbstractType<Integer> arg) {
        return new CheckedInteger(Integer.min(getValue(), arg.getValue()));
    }

    @Override
    public CheckedInteger makeMax(AbstractType<Integer> arg) {
        return new CheckedInteger(Integer.max(getValue(), arg.getValue()));
    }

    @Override
    public CheckedInteger makeCount() {
        return new CheckedInteger(Integer.bitCount(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

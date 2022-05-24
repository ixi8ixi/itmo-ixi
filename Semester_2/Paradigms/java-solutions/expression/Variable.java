package expression;

import expression.generic.types.AbstractType;

import java.util.Objects;
import java.util.function.Supplier;

public class Variable<N> implements TripleExpression<N> {
    private final String name;
    private final Supplier<AbstractType<N>> valueInstanceCreator;

    public Variable(String name, Supplier<AbstractType<N>> valueInstanceCreator) {
        this.name = name;
        this.valueInstanceCreator = valueInstanceCreator;
    }

    @Override
    public AbstractType<N> evaluate(int x, int y, int z) {
        AbstractType<N> result = valueInstanceCreator.get();
        switch (name) {
            case "x" -> result.setIntValue(x);
            case "y" -> result.setIntValue(y);
            case "z" -> result.setIntValue(z);
            default -> throw new IllegalStateException("Illegal variable name: " + name);
        }
        return result;
    }

    @Override
    public String toMiniString() {
        return name;
    }

    @Override
    public String toString() {
        return name;
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

    @Override
    public String getOperationString() {
        return null;
    }

    @Override
    public void makeString(StringBuilder in) {
        in.append(name);
    }

    @Override
    public void makeMiniString(StringBuilder in) {
        in.append(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable<?> variable = (Variable<?>) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

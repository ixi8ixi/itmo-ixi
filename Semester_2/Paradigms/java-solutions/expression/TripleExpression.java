package expression;

import expression.generic.types.AbstractType;

public interface TripleExpression<N> extends ToMiniString {
    int getPriority();
    int getRightInOperationPriority();
    int getLeftInOperationPriority();
    String getOperationString();
    void makeString(StringBuilder in);
    void makeMiniString(StringBuilder in);

    AbstractType<N> evaluate(int x, int y, int z);
}

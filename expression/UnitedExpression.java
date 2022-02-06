package expression;

public interface UnitedExpression extends Expression, TripleExpression, BigIntegerExpression, ToMiniString {
    int getPriority();
    String getOperationString();
}
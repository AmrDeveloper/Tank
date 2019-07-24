package ast;

import visitors.ExpressionVisitor;

public class GroupingExp extends Expression{

    private Expression expression;

    public GroupingExp(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

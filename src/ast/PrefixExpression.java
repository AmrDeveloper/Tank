package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class PrefixExpression extends Expression {

    private final Token prefixFunName;
    private final Expression rightExpression;

    public PrefixExpression(Token prefixFunName, Expression rightExpression) {
        this.prefixFunName = prefixFunName;
        this.rightExpression = rightExpression;
    }

    public Token getPrefixOperatorName() {
        return prefixFunName;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

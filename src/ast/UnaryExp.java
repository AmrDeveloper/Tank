package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class UnaryExp extends Expression {

    private Token operator;
    private Expression rightExp;

    public UnaryExp(Token operator, Expression rightExp) {
        this.operator = operator;
        this.rightExp = rightExp;
    }

    public Token getOperator() {
        return operator;
    }

    public Expression getRightExp() {
        return rightExp;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class BinaryExp extends Expression {

    private final Expression leftExp;
    private final Token operator;
    private final Expression rightExp;

    public BinaryExp(Expression leftExp, Token operator, Expression rightExp) {
        this.leftExp = leftExp;
        this.operator = operator;
        this.rightExp = rightExp;
    }

    public Expression getLeftExp() {
        return leftExp;
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

package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class InfixExpression extends Expression {

    private final Expression leftExp;
    private final Token infixOperator;
    private final Expression rightExp;

    public InfixExpression(Expression leftExp, Token infixOperator, Expression rightExp) {
        this.leftExp = leftExp;
        this.infixOperator = infixOperator;
        this.rightExp = rightExp;
    }

    public Expression getLeftExp() {
        return leftExp;
    }

    public Token getInfixOperatorName() {
        return infixOperator;
    }

    public Expression getRightExp() {
        return rightExp;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

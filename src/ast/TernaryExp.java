package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class TernaryExp extends Expression{

    private Expression condition;
    private Token ternaryOp;
    private Expression firstExp;
    private Token colon;
    private Expression secondExp;

    public TernaryExp(Expression condition, Token ternaryOp,
                      Expression firstExp, Token colon,
                      Expression secondExp) {
        this.condition = condition;
        this.ternaryOp = ternaryOp;
        this.firstExp = firstExp;
        this.colon = colon;
        this.secondExp = secondExp;
    }

    public Expression getCondition() {
        return condition;
    }

    public Token getTernaryOp() {
        return ternaryOp;
    }

    public Expression getFirstExp() {
        return firstExp;
    }

    public Token getColon() {
        return colon;
    }

    public Expression getSecondExp() {
        return secondExp;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class ElvisExp extends Expression {

    private Expression condition;
    private Token elvisOpe;
    private Expression rightExp;

    public ElvisExp(Expression condition,
                    Token elvisOpe,
                    Expression rightExp) {
        this.condition = condition;
        this.elvisOpe = elvisOpe;
        this.rightExp = rightExp;
    }

    public Expression getCondition() {
        return condition;
    }

    public Token getElvisOpe() {
        return elvisOpe;
    }

    public Expression getRightExp() {
        return rightExp;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

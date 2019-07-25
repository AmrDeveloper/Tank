package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class SetExp extends Expression {

    private Expression object;
    private Token name;
    private Expression value;

    public SetExp(Expression object, Token name, Expression value) {
        this.object = object;
        this.name = name;
        this.value = value;
    }

    public Expression getObject() {
        return object;
    }

    public Token getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

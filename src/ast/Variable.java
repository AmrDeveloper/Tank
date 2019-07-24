package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class Variable extends Expression{

    private Token name;

    public Variable(Token name) {
        this.name = name;
    }

    public Token getName() {
        return name;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

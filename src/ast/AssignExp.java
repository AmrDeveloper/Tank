package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class AssignExp extends Expression {

    private Token name;
    private Expression value;

    public AssignExp(Token name, Expression value) {
        this.name = name;
        this.value = value;
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

package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class GetExp extends Expression {

    private Token name;
    private Expression object;

    public GetExp(Token name, Expression object) {
        this.name = name;
        this.object = object;
    }

    public Token getName() {
        return name;
    }

    public Expression getObject() {
        return object;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

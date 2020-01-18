package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class ArraySetExp extends Expression{

    private Token name;
    private Expression index;
    private Expression value;

    public ArraySetExp(Token name, Expression index, Expression value) {
        this.name = name;
        this.index = index;
        this.value = value;
    }

    public Token getName() {
        return name;
    }

    public Expression getIndex() {
        return index;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

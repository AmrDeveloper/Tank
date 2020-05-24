package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class ArrayVariable extends Expression{

    private final Token name;
    private final Expression index;

    public ArrayVariable(Token name, Expression index) {
        this.name = name;
        this.index = index;
    }

    public Token getName() {
        return name;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

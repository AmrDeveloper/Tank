package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class VariableIndex extends Expression{

    private Token name;
    private Expression index;

    public VariableIndex(Token name, Expression index) {
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

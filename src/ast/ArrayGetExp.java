package ast;

import visitors.ExpressionVisitor;

public class ArrayGetExp extends Expression {

    private final Expression size;

    public ArrayGetExp(Expression size){
        this.size = size;
    }

    public Expression getSize() {
        return size;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

package ast;

import visitors.ExpressionVisitor;

public abstract class Expression {
    public abstract <R> R accept(ExpressionVisitor<R> visitor);
}

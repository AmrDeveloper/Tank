package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class ThisExp extends Expression {

    private Token keyword;

    public ThisExp(Token keyword) {
        this.keyword = keyword;
    }

    public Token getKeyword() {
        return keyword;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

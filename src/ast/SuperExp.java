package ast;

import token.Token;
import visitors.ExpressionVisitor;

public class SuperExp extends Expression {

    private Token keyword;
    private Token method;

    public SuperExp(Token keyword, Token method) {
        this.keyword = keyword;
        this.method = method;
    }

    public Token getKeyword() {
        return keyword;
    }

    public Token getMethod() {
        return method;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

package ast;

import token.Token;
import visitors.StatementVisitor;

public class ReturnStatement extends Statement {

    private Token keyword;
    private Expression value;

    public ReturnStatement(Token keyword, Expression value) {
        this.keyword = keyword;
        this.value = value;
    }

    public Token getKeyword() {
        return keyword;
    }

    public Expression getValue() {
        return value;
    }


    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

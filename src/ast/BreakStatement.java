package ast;

import token.Token;
import visitors.StatementVisitor;

public class BreakStatement extends Statement {

    private Token keyword;

    public BreakStatement(Token keyword){
        this.keyword = keyword;
    }

    public Token getKeyword() {
        return keyword;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

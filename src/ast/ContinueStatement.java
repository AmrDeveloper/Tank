package ast;

import visitors.StatementVisitor;

public class ContinueStatement extends Statement {

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

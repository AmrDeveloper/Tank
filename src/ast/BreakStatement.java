package ast;

import visitors.StatementVisitor;

public class BreakStatement extends Statement {

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

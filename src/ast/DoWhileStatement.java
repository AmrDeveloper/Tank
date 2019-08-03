package ast;

import visitors.StatementVisitor;

public class DoWhileStatement extends WhileStatement {

    private Expression condition;
    private Statement loopBody;

    public DoWhileStatement(Expression condition, Statement loopBody) {
        super(condition, loopBody);
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

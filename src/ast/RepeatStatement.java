package ast;

import visitors.StatementVisitor;

public class RepeatStatement extends Statement {

    private Expression value;
    private Statement loopBody;

    public RepeatStatement(Expression value, Statement loopBody) {
        this.value = value;
        this.loopBody = loopBody;
    }

    public Expression getValue() {
        return value;
    }

    public Statement getLoopBody() {
        return loopBody;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

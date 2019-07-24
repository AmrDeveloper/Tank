package ast;

import visitors.StatementVisitor;

import java.util.List;

public class WhileStatement extends Statement {

    private Expression condition;
    private List<Statement> statementList;

    public WhileStatement(Expression condition, List<Statement> statementList) {
        this.condition = condition;
        this.statementList = statementList;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getLoopBody() {
        return statementList;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

package ast;

import visitors.StatementVisitor;

import java.util.List;

public class RepeatStatement extends Statement {

    private Expression value;
    private List<Statement> statementList;

    public RepeatStatement(Expression value, List<Statement> statementList) {
        this.value = value;
        this.statementList = statementList;
    }

    public Expression getValue() {
        return value;
    }

    public List<Statement> getStatementList() {
        return statementList;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

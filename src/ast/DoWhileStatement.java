package ast;

import visitors.StatementVisitor;

import java.util.List;

public class DoWhileStatement extends WhileStatement {

    private Expression condition;
    private List<Statement> statementList;

    public DoWhileStatement(Expression condition, List<Statement> statementList) {
        super(condition, statementList);
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

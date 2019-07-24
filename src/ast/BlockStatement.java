package ast;

import visitors.StatementVisitor;

import java.util.List;

public class BlockStatement extends Statement{

    private List<Statement> statementList;

    public BlockStatement(List<Statement> statementList) {
        this.statementList = statementList;
    }

    public List<Statement> getStatementList() {
        return statementList;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

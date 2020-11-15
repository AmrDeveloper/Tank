package ast;

import token.Token;
import visitors.StatementVisitor;

import java.util.List;

public class TestStatement extends Statement {

    private Token name;
    private List<Statement> body;
    private Statement returnValue;

    public TestStatement(Token name, List<Statement> body, Statement returnValue) {
        this.name = name;
        this.body = body;
        this.returnValue = returnValue;
    }

    public Token getName() {
        return name;
    }

    public List<Statement> getBody() {
        return body;
    }

    public Statement getReturnValue() {
        return returnValue;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

package ast;

import token.Token;
import visitors.StatementVisitor;

import java.util.List;

public class FunctionStatement extends Function {

    private Token name;
    private List<Token> params;
    private List<Statement> funcBody;

    public FunctionStatement(Token name,
                             List<Token> params,
                             List<Statement> funcBody) {
        this.name = name;
        this.params = params;
        this.funcBody = funcBody;
    }

    public Token getName() {
        return name;
    }

    public List<Token> getParams() {
        return params;
    }

    public List<Statement> getFunctionBody() {
        return funcBody;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

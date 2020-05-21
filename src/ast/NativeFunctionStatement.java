package ast;

import token.Token;
import visitors.StatementVisitor;

import java.util.List;

public class NativeFunctionStatement extends Function{

    private final Token name;
    private final Token moduleName;
    private final List<Token> params;

    public NativeFunctionStatement(Token name, Token moduleName, List<Token> params) {
        this.name = name;
        this.moduleName = moduleName;
        this.params = params;
    }

    public Token getName() {
        return name;
    }

    public Token getModuleName() {
        return moduleName;
    }

    public List<Token> getParams() {
        return params;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

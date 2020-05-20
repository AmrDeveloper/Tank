package ast;

import token.Token;
import visitors.StatementVisitor;

public class ModuleStatement extends Statement {

    private final Token name;

    public ModuleStatement(Token name) {
        this.name = name;
    }

    public Token getName() {
        return name;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

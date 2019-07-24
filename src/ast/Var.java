package ast;

import token.Token;
import visitors.StatementVisitor;

public class Var extends Statement {

    private Token name;
    private Expression initializer;

    public Var(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public Token getName() {
        return name;
    }

    public Expression getInitializer() {
        return initializer;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

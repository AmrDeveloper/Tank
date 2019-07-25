package ast;

import token.Token;
import visitors.StatementVisitor;

import java.util.List;

public class ClassStatement extends Statement{

    private Token name;
    private List<FunctionStatement> methods;
    //Add variables letter will be list too

    public ClassStatement(Token name, List<FunctionStatement> methods) {
        this.name = name;
        this.methods = methods;
    }

    public Token getName() {
        return name;
    }

    public List<FunctionStatement> getMethods() {
        return methods;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

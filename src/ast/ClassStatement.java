package ast;

import token.Token;
import visitors.StatementVisitor;

import java.util.List;

public class ClassStatement extends Statement{

    private Token name;
    private Variable superClass;
    private List<FunctionStatement> methods;

    public ClassStatement(Token name, Variable superClass,List<FunctionStatement> methods) {
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
    }

    public Token getName() {
        return name;
    }

    public Variable getSuperClass() {
        return superClass;
    }

    public List<FunctionStatement> getMethods() {
        return methods;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

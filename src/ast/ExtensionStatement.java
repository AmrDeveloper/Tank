package ast;

import token.Token;
import visitors.StatementVisitor;

public class ExtensionStatement extends Function {

    private Token className;
    private FunctionStatement functionStatement;

    public ExtensionStatement(Token className, FunctionStatement function) {
        this.className = className;
        this.functionStatement = function;
    }

    public Token getClassName(){
        return className;
    }

    public FunctionStatement getFunctionStatement(){
        return functionStatement;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

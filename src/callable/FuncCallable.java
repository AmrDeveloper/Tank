package callable;

import ast.FunctionStatement;
import interpreter.Environment;
import interpreter.Interpreter;
import token.Token;

import java.util.List;

public class FuncCallable implements TankCallable {

    private FunctionStatement declaration;

    public FuncCallable(FunctionStatement declaration){
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(interpreter.getGlobalsEnvironment());
        for(int i = 0 ; i < declaration.getParams().size() ; i++){
            environment.define(declaration.getParams().get(i).lexeme,arguments.get(i));
        }
        interpreter.executeBlock(declaration.getFunctionBody(),environment);
        return null;
    }
}

package callable;

import ast.FunctionStatement;
import interpreter.Environment;
import interpreter.Interpreter;
import runtime.Return;

import java.util.List;

public class TankFunction implements TankCallable {

    private FunctionStatement declaration;
    private final Environment closure;

    public TankFunction(FunctionStatement declaration, Environment closure){
        this.closure = closure;
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for(int i = 0 ; i < declaration.getParams().size() ; i++){
            environment.define(declaration.getParams().get(i).lexeme,arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.getFunctionBody(), environment);
        } catch (Return returnValue) {
            return returnValue.getValue();
        }
        return null;
    }
}

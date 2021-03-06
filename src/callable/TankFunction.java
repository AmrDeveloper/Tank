package callable;

import ast.FunctionStatement;
import interpreter.Environment;
import interpreter.Interpreter;
import runtime.Return;

import java.util.List;

public class TankFunction implements TankCallable {

    private final FunctionStatement declaration;
    private final Environment closure;

    private final boolean isInitializer;

    public TankFunction(FunctionStatement declaration, Environment closure,boolean isInitializer){
        this.closure = closure;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    public TankFunction bind(TankInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new TankFunction(declaration, environment,isInitializer);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for(int i = 0 ; i < declaration.getParams().size() ; i++){
            environment.define(declaration.getParams().get(i).lexeme,arguments.get(i));
        }
        try {
            interpreter.execute(declaration.getFunctionBody(), environment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");
            return returnValue.getValue();
        }
        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }
}

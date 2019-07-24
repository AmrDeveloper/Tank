package callable;

import interpreter.Interpreter;

import java.util.List;

public interface TankCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}

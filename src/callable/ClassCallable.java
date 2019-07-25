package callable;

import interpreter.Interpreter;

import java.util.List;

public class ClassCallable implements TankCallable{

    private final String name;

    public ClassCallable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        TankInstance instance = new TankInstance(this);
        return instance;
    }
}

package callable;

import interpreter.Interpreter;

import java.util.List;
import java.util.Map;

public class TankClass implements TankCallable{

    private final String name;
    private Map<String, TankFunction> methods;

    public TankClass(String name, Map<String, TankFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public Map<String, TankFunction> getMethods() {
        return methods;
    }

    public TankFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }
        return null;
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

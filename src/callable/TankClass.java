package callable;

import interpreter.Interpreter;

import java.util.List;
import java.util.Map;

public class TankClass implements TankCallable{

    private final String name;
    private final TankClass superClass;
    private final Map<String, TankFunction> methods;

    public TankClass(String name, TankClass superClass,Map<String, TankFunction> methods) {
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public TankClass getSuperClass() {
        return superClass;
    }

    public Map<String, TankFunction> getMethods() {
        return methods;
    }

    public TankFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }
        //if can't find this method in class check if this method is from super class
        if (superClass != null) {
            return superClass.findMethod(name);
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        TankFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        TankInstance instance = new TankInstance(this);
        //for Constructing the class
        TankFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }
}

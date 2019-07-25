package callable;

import runtime.RuntimeError;
import token.Token;

import java.util.HashMap;
import java.util.Map;

public class TankInstance {

    private ClassCallable classCallable;
    private final Map<String, Object> fields = new HashMap<>();

    public TankInstance(ClassCallable classCallable) {
        this.classCallable = classCallable;
    }

    public ClassCallable getClassCallable() {
        return classCallable;
    }

    public void set(Token name,Object value){
        fields.put(name.lexeme,value);
    }

    public Object get(Token name){
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }
        throw new RuntimeError(name,"Undefined property '" + name.lexeme + "'.");
    }

    @Override
    public String toString() {
        return classCallable.getName() + " instance";
    }
}

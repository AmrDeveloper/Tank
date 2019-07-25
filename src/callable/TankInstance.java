package callable;

import runtime.RuntimeError;
import token.Token;

import java.util.HashMap;
import java.util.Map;

public class TankInstance {

    private TankClass tankClass;
    private final Map<String, Object> fields = new HashMap<>();

    public TankInstance(TankClass tankClass) {
        this.tankClass = tankClass;
    }

    public TankClass getTankClass() {
        return tankClass;
    }

    public void set(Token name,Object value){
        fields.put(name.lexeme,value);
    }

    public Object get(Token name){
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }
        TankFunction method = tankClass.findMethod(name.lexeme);
        if (method != null) return method;
        throw new RuntimeError(name,"Undefined property '" + name.lexeme + "'.");
    }

    @Override
    public String toString() {
        return tankClass.getName() + " instance";
    }
}

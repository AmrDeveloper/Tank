package interpreter;

import runtime.RuntimeError;
import token.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    public final Environment enclosing;
    private final Map<String, Object> valuesMap = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Object get(Token name) {
        if (valuesMap.containsKey(name.lexeme)) {
            return valuesMap.get(name.lexeme);
        }

        //If the variable isn’t found in this scope, we simply try the enclosing one
        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    public void define(String name, Object value) {
        valuesMap.put(name, value);
    }

    public boolean isContain(String name){
        return valuesMap.containsKey(name);
    }

    public void assign(Token name, Object value) {
        if (valuesMap.containsKey(name.lexeme)) {
            valuesMap.put(name.lexeme, value);
            return;
        }
        //Again, if the variable isn’t in this environment, it checks the outer one, recursively.
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).valuesMap.put(name.lexeme, value);
    }

    public Object getAt(int distance, String name) {
        return ancestor(distance).valuesMap.get(name);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }
}

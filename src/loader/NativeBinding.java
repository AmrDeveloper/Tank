package loader;

import callable.TankCallable;
import interpreter.Environment;
import interpreter.Interpreter;

import java.util.List;

public class NativeBinding {

    public static void bindNativeFunction(Environment global, NativeFunction... functions){
        for(NativeFunction nativeFunc : functions){
            global.define(nativeFunc.getName(), new TankCallable() {
                @Override
                public int arity() {
                    return nativeFunc.getArgsNum();
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return nativeFunc.getFunctionValue().createTask(arguments);
                }

                @Override
                public String toString() {
                    return "<native functions>";
                }
            });
        }
    }
}

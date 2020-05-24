package callable;

import ast.NativeFunctionStatement;
import interpreter.Interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TankLibrary implements TankCallable {

    private final NativeFunctionStatement declaration;

    public TankLibrary(NativeFunctionStatement declaration) {
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        String moduleName = declaration.getModuleName().lexeme;
        String functionName = declaration.getName().lexeme;

        try {
            Class moduleClass = Class.forName("modules." + moduleName);
            Method method;
            if (arguments.size() == 0) {
                method = moduleClass.getMethod(functionName);
                return method.invoke(moduleClass.newInstance());
            } else {
                List<Class> classArgs = new ArrayList<>();
                for (Object arg : arguments) {
                    classArgs.add(arg.getClass());
                }
                method = moduleClass.getMethod(functionName, classArgs.toArray(new Class[0]));
                return method.invoke(moduleClass.newInstance(), arguments.toArray(new Object[0]));
            }
        } catch (InstantiationException e) {
            System.err.println("Invalid Module Loader : " + e.getMessage());
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Invalid Module Loader : " + e.getMessage());
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("Module Loader : Can't find class with name : " + functionName);
            System.exit(1);
        } catch (NoSuchMethodException e) {
            System.err.println("Module Loader : Can't find function with name : " + functionName);
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println("Module Loader : Can't access module " + moduleName);
            System.exit(1);
        }
        return null;
    }
}

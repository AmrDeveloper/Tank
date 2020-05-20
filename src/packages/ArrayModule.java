package packages;

import ast.Array;
import interpreter.Environment;
import nativefunc.Module;
import nativefunc.NativeBinding;
import nativefunc.NativeFunction;

public class ArrayModule implements Module {

    private final NativeFunction arrayLength = new NativeFunction("arrayLength", 1, args -> {
        if (args.size() != 1) {
            throw new RuntimeException("Invalid number of argument in len function");
        }
        Object argOne = args.get(0);
        if (argOne instanceof Array) {
            return ((Array) argOne).getLength();
        } else {
            throw new RuntimeException("Argument mush be string type");
        }
    });

    @Override
    public void loadLibraries(Environment environment) {
        NativeBinding.bindNativeFunction(environment, arrayLength);
    }
}

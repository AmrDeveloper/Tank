package packages;

import ast.Array;
import interpreter.Environment;
import nativefunc.NativeBinding;
import nativefunc.NativeFunction;
import nativefunc.NativePackage;

public class ArrayPackage implements NativePackage {

    private NativeFunction arrayLength = new NativeFunction("arrayLength", 1, args -> {
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
    public void bindNativeFunction(Environment global) {
        NativeBinding.bindNativeFunction(global, arrayLength);
    }
}

package packages;

import interpreter.Environment;
import nativefunc.NativeBinding;
import nativefunc.NativeFunction;
import nativefunc.NativePackage;

public class StringPackage implements NativePackage {

    private NativeFunction stringLength = new NativeFunction("len", 1, args -> {
        if(args.size() != 1){
           throw new RuntimeException("invalid number of argument in len function");
        }
        return args.get(0).toString().length();
    });

    private NativeFunction stringIndex = new NativeFunction("charAt", 2, args -> {
        if(args.size() != 2){
            throw new RuntimeException("invalid number of argument in len function");
        }
        String string = args.get(0).toString();
        int index = (int)Double.parseDouble(args.get(1).toString());
        return string.charAt(index);
    });

    @Override
    public void bindNativeFunction(Environment global) {
        NativeBinding.bindNativeFunction(global,
                stringLength,
                stringIndex);
    }
}

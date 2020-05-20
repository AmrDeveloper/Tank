package modules;

import interpreter.Environment;
import loader.Module;
import loader.NativeBinding;
import loader.NativeFunction;

public class StringModule implements Module {

    private NativeFunction stringLength = new NativeFunction("len", 1, args -> {
        if (args.size() != 1) {
            throw new RuntimeException("Invalid number of argument in len function");
        }
        if (args.get(0) instanceof String) {
            return args.get(0).toString().length();
        } else {
            throw new RuntimeException("Argument mush be string type");
        }
    });

    private NativeFunction stringIndex = new NativeFunction("charAt", 2, args -> {
        if (args.size() != 2) {
            throw new RuntimeException("invalid number of argument in len function");
        }
        if (args.get(0) instanceof String) {
            String string = args.get(0).toString();
            int index = (int) Double.parseDouble(args.get(1).toString());
            return string.charAt(index);
        } else {
            throw new RuntimeException("Argument mush be string type");
        }
    });

    @Override
    public void loadLibraries(Environment environment) {
        NativeBinding.bindNativeFunction(environment,
                stringLength,
                stringIndex);
    }
}

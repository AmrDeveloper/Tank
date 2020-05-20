package modules;

import interpreter.Environment;
import loader.Module;
import loader.NativeBinding;
import loader.NativeFunction;

public class TimeModule implements Module {

    private final NativeFunction currentTime = new NativeFunction("currentTime", 0, args -> (double) System.currentTimeMillis());

    @Override
    public void loadLibraries(Environment environment) {
        NativeBinding.bindNativeFunction(environment, currentTime);
    }
}

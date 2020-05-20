package packages;

import interpreter.Environment;
import nativefunc.Module;
import nativefunc.NativeBinding;
import nativefunc.NativeFunction;

public class TimeModule implements Module {

    private final NativeFunction currentTime = new NativeFunction("currentTime", 0, args -> (double) System.currentTimeMillis());

    @Override
    public void loadLibraries(Environment environment) {
        NativeBinding.bindNativeFunction(environment, currentTime);
    }
}

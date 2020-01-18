package packages;

import interpreter.Environment;
import nativefunc.NativeBinding;
import nativefunc.NativeFunction;
import nativefunc.NativePackage;

public class TimePackage implements NativePackage {

    private NativeFunction currentTime = new NativeFunction("time", 0, args -> (double) System.currentTimeMillis());

    @Override
    public void bindNativeFunction(Environment global) {
        NativeBinding.bindNativeFunction(global, currentTime);
    }
}

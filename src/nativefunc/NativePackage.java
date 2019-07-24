package nativefunc;

import interpreter.Environment;

@FunctionalInterface
public interface NativePackage {
    void bindNativeFunction(Environment global);
}

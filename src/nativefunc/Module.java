package nativefunc;

import interpreter.Environment;

@FunctionalInterface
public interface Module {
    public void loadLibraries(Environment environment);
}

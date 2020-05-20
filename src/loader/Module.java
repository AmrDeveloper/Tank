package loader;

import interpreter.Environment;

@FunctionalInterface
public interface Module {
    public void loadLibraries(Environment environment);
}

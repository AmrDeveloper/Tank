package nativefunc;

@FunctionalInterface
public interface FunctionValue {
    Object createTask(Object...args);
}

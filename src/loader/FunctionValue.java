package loader;

import java.util.List;

@FunctionalInterface
public interface FunctionValue {
    Object createTask(List<Object> args);
}

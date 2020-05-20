package nativefunc;

public class NativeFunction {

    private final String name;
    private final int argsNum;
    private final FunctionValue funcBody;

    public NativeFunction(String name, int argsNum, FunctionValue funcBody) {
        this.name = name;
        this.argsNum = argsNum;
        this.funcBody = funcBody;
    }

    public String getName() {
        return name;
    }

    public int getArgsNum() {
        return argsNum;
    }

    public FunctionValue getFunctionValue() {
        return funcBody;
    }
}

package nativefunc;

public class NativeFunction {

    private String name;
    private int argsNum;
    private FunctionValue funcBody;

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

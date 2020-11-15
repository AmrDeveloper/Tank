import runtime.TankRuntime;

public class Tank {

    public static void main(String[] args) {
        String sourceFile = "examples/TestStatement.tank";
        TankRuntime.runTankFile(sourceFile);
        //TankRuntime.runTankTerminal();
    }
}

import runtime.TankRuntime;

public class Tank {

    public static void main(String[] args) {
        String sourceFile = "examples/Style.tank";
        TankRuntime.checkFileCodeStyle(sourceFile);
        //TankRuntime.runTankFile(sourceFile);
        //TankRuntime.runTankTerminal();
    }
}

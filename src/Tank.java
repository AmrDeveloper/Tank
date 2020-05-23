import runtime.TankRuntime;

public class Tank {

    public static void main(String[] args) {
        String sourceFile = "examples/Mod.tank";
        TankRuntime.runTankFile(sourceFile);
        //TankRuntime.runTankTerminal();
    }
}

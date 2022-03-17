import runtime.TankRuntime;

import java.io.IOException;

public class Tank {

    public static void main(String[] args) throws IOException {
        String sourceFile = "examples/Prefix.tank";
        //TankRuntime.checkFileCodeStyle(sourceFile);
        TankRuntime.runTankFile(sourceFile);
        //TankRuntime.runTankTerminal();
    }
}

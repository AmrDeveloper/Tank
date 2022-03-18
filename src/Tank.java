import runtime.TankRuntime;

import java.io.IOException;

public class Tank {

    enum TankOptions {
        OPTION_RUN_FILE,
        OPTION_RUN_REPL,
        OPTION_RUN_LINT,
    }

    private static final TankOptions currentOption = TankOptions.OPTION_RUN_FILE;

    public static void main(String[] args) throws IOException {
        String sourceFile = "examples/InfixPrefix.tank";
        switch (currentOption) {
            case OPTION_RUN_FILE: {
                TankRuntime.runTankFile(sourceFile);
                break;
            }
            case OPTION_RUN_REPL: {
                TankRuntime.runTankTerminal();
                break;
            }
            case OPTION_RUN_LINT: {
                TankRuntime.checkFileCodeStyle(sourceFile);
                break;
            }
        }
    }
}

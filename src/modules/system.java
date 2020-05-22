package modules;

import java.io.IOException;

@Library
public class system {

    @LibraryFunction
    public void winTerminal( ) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd.exe /c start", null);
    }

    @LibraryFunction
    public String osName() {
        return System.getProperty("os.name");
    }

    @LibraryFunction
    public String osEnvVar(String program) {
        return System.getenv(program);
    }

    @LibraryFunction
    public String osArch( ) {
        return System.getProperty("os.arch");
    }
}


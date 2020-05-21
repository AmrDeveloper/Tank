package modules;

import java.io.IOException;

@Library
public class system {

    @LibraryFunction
    public void openTerminal( ) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd.exe /c start", null);
    }

    @LibraryFunction
    public void testArgs (String name) {
        System.out.println("hello");
    }
}


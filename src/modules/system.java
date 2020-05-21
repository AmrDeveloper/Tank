package modules;

import java.io.IOException;

public class system {

    public void openTerminal( ) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd.exe /c start", null);
    }

    public void testArgs (String name) {
        System.out.println("hello");
    }
}


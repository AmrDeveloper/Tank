package modules;

import interpreter.Environment;
import loader.Module;
import loader.NativeBinding;
import loader.NativeFunction;

import java.util.Scanner;

public class ScanModule implements Module {

    private final NativeFunction stringScanner = new NativeFunction("scanString", 0, args -> {
        Scanner input = new Scanner(System.in);
        return input.next();
    });

    private final NativeFunction numberScanner = new NativeFunction("scanNumber", 0, args -> {
        Scanner input = new Scanner(System.in);
        return input.nextDouble();
    });

    @Override
    public void loadLibraries(Environment global) {
        NativeBinding.bindNativeFunction(global, stringScanner);
        NativeBinding.bindNativeFunction(global, numberScanner);
    }
}

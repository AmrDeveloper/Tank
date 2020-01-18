package packages;

import interpreter.Environment;
import nativefunc.NativeBinding;
import nativefunc.NativeFunction;
import nativefunc.NativePackage;

import java.util.Scanner;

public class ScanPackage implements NativePackage {

    private NativeFunction stringScanner = new NativeFunction("scanString", 0, args -> {
        Scanner input = new Scanner(System.in);
        return input.next();
    });

    private NativeFunction numberScanner = new NativeFunction("scanNumber", 0, args -> {
        Scanner input = new Scanner(System.in);
        return input.nextDouble();
    });

    @Override
    public void bindNativeFunction(Environment global) {
        NativeBinding.bindNativeFunction(global, stringScanner);
        NativeBinding.bindNativeFunction(global, numberScanner);
    }
}

package nativefunc;

import packages.ArrayPackage;
import packages.ScanPackage;
import packages.StringPackage;
import packages.TimePackage;

public class Packages {

    private static final NativePackage[] nativePackages = {
            new TimePackage(),
            new ScanPackage(),
            new StringPackage(),
            new ArrayPackage()
    };

    public static NativePackage[] getNativePackages(){
        return nativePackages;
    }
}

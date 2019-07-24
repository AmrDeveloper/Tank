package nativefunc;

import packages.ScanPackage;
import packages.TimePackage;

public class Packages {

    private static final NativePackage[] nativePackages = {
            new TimePackage(),
            new ScanPackage(),
    };

    public static NativePackage[] getNativePackages(){
        return nativePackages;
    }
}

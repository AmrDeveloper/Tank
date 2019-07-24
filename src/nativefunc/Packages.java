package nativefunc;

import packages.ScanPackage;
import packages.StringPackage;
import packages.TimePackage;

public class Packages {

    private static final NativePackage[] nativePackages = {
            new TimePackage(),
            new ScanPackage(),
            new StringPackage()
    };

    public static NativePackage[] getNativePackages(){
        return nativePackages;
    }
}

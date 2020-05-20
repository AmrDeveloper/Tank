package loader;

import interpreter.Environment;

public class ModuleLoader {

    public static void loadEnvironmentModule(Environment environment, String moduleName) {
        String moduleNameCap = moduleName.substring(0, 1).toUpperCase() + moduleName.substring(1);
        String moduleClassName = moduleNameCap.concat("Module");
        try{
            Class moduleClass = Class.forName("modules." + moduleClassName);
            Module moduleInstance = (Module) moduleClass.newInstance();
            moduleInstance.loadLibraries(environment);
        }catch (Exception e){
            System.out.println("Invalid Module Loader : " + e.getMessage());
        }
    }
}

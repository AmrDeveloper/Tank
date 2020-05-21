package lexer;

import runtime.TankRuntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ModuleProcessor {

    private String currentFilePath;
    private final Stack<String> modulesStack = new Stack<>();
    private final Set<String> scannedModules = new HashSet<>();

    public String process(String filePath) {
        this.modulesStack.add(filePath);
        this.scannedModules.add(filePath);
        this.currentFilePath = filePath;
        StringBuilder source = new StringBuilder();
        String currentFileSource = readFileSource(filePath);
        while (!modulesStack.isEmpty()){
            currentFilePath = modulesStack.pop();
            currentFileSource = readFileSource(currentFilePath);
            source = source.append(currentFileSource).append(System.getProperty("line.separator"));
        }
        return source.toString();
    }

    private String readFileSource(String path) {
        StringBuilder lines = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.trim().startsWith("module")) {
                    String moduleName =
                            line.replace("module", "")
                                    .replaceAll(";", "")
                                    .trim();
                    scanModuleName(moduleName);
                } else{
                    lines.append(line);
                }
            }
        } catch (IOException e) {
            TankRuntime.error("Invalid File path : " + path);
            System.exit(1);
        }
        return lines.toString();
    }

    private void scanModuleName(String moduleNameStr) {
        String modulePath = "";
        if(moduleNameStr.startsWith("tank.")){
            modulePath = "src/modules/" + moduleNameStr.substring(5) + ".tank";
        } else {
            String parentPath = new File(currentFilePath).getParent();
            modulePath = parentPath + "/" + moduleNameStr + ".tank";
        }

        boolean isUnique = scannedModules.add(modulePath);
        if(isUnique){
            modulesStack.add(modulePath);
        }
    }
}

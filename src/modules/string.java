package modules;

@Library
public class string {

    @LibraryFunction
    public int strLength(String string) {
        return string.length();
    }
}

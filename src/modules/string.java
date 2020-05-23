package modules;

@Library
public class string {

    @LibraryFunction
    public int strLength(String string) {
        return string.length();
    }

    @LibraryFunction
    public boolean strMatch(String str, String regex) {
        return str.matches(regex);
    }
}

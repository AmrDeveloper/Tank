package style;

import java.util.regex.Pattern;

public class CheckStyleConfig {

    public static final Pattern classNamePattern = Pattern.compile("[A-Z][a-zA-Z]*");
    public static final Pattern functionNamePattern = Pattern.compile("[a-z][a-zA-Z]*");
    public static final Pattern extensionNamePattern = Pattern.compile("[a-z][a-zA-Z]*");
    public static final Pattern paramNamePattern = Pattern.compile("[a-zA-Z]*");
    public static final Pattern varNamePattern = Pattern.compile("[a-z][a-zA-Z]*");
    public static final Pattern testNamePattern = Pattern.compile("[a-zA-Z]*");

}

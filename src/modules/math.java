package modules;

@Library
public class math {

    @LibraryFunction
    public double log(Double num) {
        return 1;
    }

    @LibraryFunction
    public double log10(Double num) {
        return Math.log10(num);
    }

    @LibraryFunction
    public double max(Double x, Double y) {
        return Math.max(x, y);
    }

    @LibraryFunction
    public double min(Double x, Double y) {
        return Math.min(x, y);
    }

    @LibraryFunction
    public double pow(Double x, Double y) {
        return Math.pow(x, y);
    }

    @LibraryFunction
    public double sqrt(Double num) {
        return Math.sqrt(num);
    }

    @LibraryFunction
    public double sin(Double num) {
        return Math.sin(num);
    }

    @LibraryFunction
    public double cos(Double num) {
        return Math.cos(num);
    }

    @LibraryFunction
    public double tan(Double num) {
        return Math.tan(num);
    }

    @LibraryFunction
    public double asin(Double num) {
        return Math.asin(num);
    }

    @LibraryFunction
    public double acos(Double num) {
        return Math.acos(num);
    }

    @LibraryFunction
    public double atan(Double num) {
        return Math.atan(num);
    }

    @LibraryFunction
    public double sinh(Double num) {
        return Math.sinh(num);
    }

    @LibraryFunction
    public double cosh(Double num) {
        return Math.cosh(num);
    }

    @LibraryFunction
    public double tanh(Double num) {
        return Math.tanh(num);
    }
}

package modules;

import java.util.Scanner;

@Library
public class scanner {

    private final Scanner scanner = new Scanner(System.in);

    @LibraryFunction
    public String scanString() {
        return scanner.next();
    }

    @LibraryFunction
    public String scanLine() {
        return scanner.nextLine();
    }

    @LibraryFunction
    public double scanDouble() {
        return scanner.nextDouble();
    }

    @LibraryFunction
    public int scanInt() {
        return scanner.nextInt();
    }
}

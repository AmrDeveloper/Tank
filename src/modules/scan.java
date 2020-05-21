package modules;

import java.util.Scanner;

@Library
public class scan {

    @LibraryFunction
    public String scanString() {
        Scanner scr = new Scanner(System.in);
        return scr.next();
    }
}

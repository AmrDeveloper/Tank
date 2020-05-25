package modules;

import ast.Array;

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

    @LibraryFunction
    public String strConcat(String first, String second) {
         return first.concat(second);
    }

    @LibraryFunction
    public boolean strContains(String str, String sequence) {
        return str.contains(sequence);
    }

    @LibraryFunction
    public String strTrim(String str) {
        return str.trim();
    }

    @LibraryFunction
    public String strUpperCase(String str) {
        return str.toUpperCase();
    }

    @LibraryFunction
    public String strLowerCase(String str) {
        return str.toLowerCase();
    }

    @LibraryFunction
    public boolean strStartWith(String str, String sequence) {
        return str.startsWith(sequence);
    }

    @LibraryFunction
    public boolean strEndWith(String str, String sequence) {
       return str.endsWith(sequence);
    }

    @LibraryFunction
    public Integer strIndexOf(String str, String sequence) {
        return str.indexOf(sequence);
    }

    @LibraryFunction
    public Integer strLastIndexOf(String str, String sequence) {
        return str.lastIndexOf(sequence);
    }

    @LibraryFunction
    public String strReplaceFirst(String str, String regex, String replacement) {
        return str.replaceFirst(regex, replacement);
    }

    @LibraryFunction
    public String strReplaceAll(String str, String regex, String replacement) {
        return str.replaceAll(regex, replacement);
    }

    @LibraryFunction
    public Character strCharAt (String str, Double index) {
        return str.charAt(index.intValue());
    }

    @LibraryFunction
    public String strSubstring (String str, Double start, Double end) {
        return str.substring(start.intValue(), end.intValue());
    }

    @LibraryFunction
    public boolean strIsEmpty(String str) {
        return str.isEmpty();
    }

    @LibraryFunction
    public Array strSplit(String str, String regex) {
        return new Array(str.split(regex));
    }

    @LibraryFunction
    public Integer strCompareTo(String first, String second) {
        return first.compareTo(second);
    }
}

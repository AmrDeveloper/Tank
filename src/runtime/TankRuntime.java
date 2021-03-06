package runtime;

import ast.Statement;
import interpreter.Interpreter;
import lexer.ModuleProcessor;
import lexer.TankLexer;
import parser.Parser;
import semantic.Resolver;
import style.TankCheckStyle;
import token.Token;
import token.TokenType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;

public class TankRuntime {

    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;
    private static final Interpreter interpreter = new Interpreter();

    public static void runTankFile(String path) {
        ModuleProcessor preProcessor = new ModuleProcessor();
        String source = preProcessor.process(path);
        runTankCode(source);
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    public static void runTankTerminal() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("Welcome in Tank Programming lang");
        while (true) {
            System.out.print("> ");
            runTankCode(reader.readLine());
            hadError = false;
        }
    }

    public static void runTankCode(String source) {
        TankLexer tankLexer = new TankLexer(source);
        List<Token> tokens = tankLexer.scanTokens();
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // Stop if there was a semantic error.
        if (hadError) return;

        //Start Tank Interpreter
        interpreter.interpret(statements);
    }

    public static void checkFileCodeStyle(String path) {
        ModuleProcessor preProcessor = new ModuleProcessor();
        String source = preProcessor.process(path);

        TankLexer tankLexer = new TankLexer(source);
        List<Token> tokens = tankLexer.scanTokens();
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();

        TankCheckStyle checkStyle = new TankCheckStyle();
        checkStyle.checkCodeStyle(statements);
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void error(String message) {
        report(message);
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    private static void report(String message) {
        System.err.println(message);
        hadError = true;
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +" \n[line " + error.getToken().line + "]");
        hadRuntimeError = true;
    }
}

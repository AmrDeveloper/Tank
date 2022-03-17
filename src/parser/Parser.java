package parser;

import ast.*;
import com.sun.xml.internal.bind.v2.model.core.ID;
import runtime.TankRuntime;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static token.TokenType.*;

public class Parser {

    /*
     * TODO : Create advanced error system for parser to show expect .... in line ....
     */
    private int current = 0;
    private final List<Token> tokens;

    private static final int MAX_NUM_OF_ARGUMENTS = 8;

    private final Set<String> prefixFunctions = new HashSet<>();
    private final Set<String> infixFunctions = new HashSet<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Statement statement() {
        if (match(IF)) return ifStatement();
        if (match(DO)) return doWhileStatement();
        if (match(WHILE)) return whileStatement();
        if (match(REPEAT)) return repeatStatement();
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(LEFT_BRACE)) return new BlockStatement(block());
        if (match(BREAK)) return breakStatement();
        if (match(CONTINUE)) return continueStatement();
        if (match(TEST)) return testStatement();
        return expressionStatement();
    }

    private Statement declaration() {
        try {
            if (match(PREFIX, INFIX) && match(FUN)) {
                return funcDeclaration(tokens.get(current - 2));
            }
            if (match(FUN)) return funcDeclaration(previous());
            if (match(VAR)) return varDeclaration();
            if (match(CLASS)) return classDeclaration();
            if (match(NATIVE)) return nativeFuncDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement classDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");

        Variable superclass = null;
        if (match(EXTENDS)) {
            consume(IDENTIFIER, "Expect superclass name.");
            superclass = new Variable(previous());
        }

        consume(LEFT_BRACE, "Expect '{' before class body.");

        List<FunctionStatement> methods = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(methodDeclaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after class body.");

        return new ClassStatement(name, superclass, methods);
    }

    private FunctionStatement methodDeclaration() {
        consume(FUN, "Expect func keyword");
        Token name = consume(IDENTIFIER, "Expect method name.");
        consume(LEFT_PAREN, "Expect '(' after method name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= MAX_NUM_OF_ARGUMENTS) {
                    error(peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " parameters.");
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before method body.");
        List<Statement> body = block();
        return new FunctionStatement(name, parameters, body);
    }

    private Function funcDeclaration(Token token) {
        Token name = consume(IDENTIFIER, "Expect function name.");
        switch (token.type) {
            case PREFIX:  {
                prefixFunctions.add(name.lexeme);
                break;
            }
            case INFIX:  {
                infixFunctions.add(name.lexeme);
                break;
            }
        }

        Token extensionName = null;

        if(peek().type == COLON){
            consume(COLON, "Expect : After Class name");
            extensionName = consume(IDENTIFIER, "Expect extension name.");
        }

        consume(LEFT_PAREN, "Expect '(' after function name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= MAX_NUM_OF_ARGUMENTS) {
                    error(peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " parameters.");
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before function body.");
        List<Statement> body = block();

        if(extensionName != null) {
            FunctionStatement function = new FunctionStatement(extensionName, parameters, body);
            return new ExtensionStatement(name, function);
        }else{
            return new FunctionStatement(name, parameters, body);
        }
    }

    private Statement nativeFuncDeclaration() {
        consume(FUN, "Expect func keyword");
        Token moduleName = consume(IDENTIFIER, "Expect module name.");

        while (match(COLON)) {
            Token token = new Token(DOT, ".", moduleName.line);
            moduleName.concat(token);
            Token moduleName2 = consume(IDENTIFIER, "Expect module name.");
            moduleName.concat(moduleName2);
        }

        consume(DOT, "Expect '.' before method body.");
        Token funcName = consume(IDENTIFIER, "Expect function name.");
        consume(LEFT_PAREN, "Expect '(' after method name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= MAX_NUM_OF_ARGUMENTS) {
                    error(peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " parameters.");
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(SEMICOLON, "Expect ';' after value.");
        return new NativeFunctionStatement(funcName, moduleName ,parameters);
    }

    private Statement varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expression initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Var(name, initializer);
    }

    private Statement breakStatement() {
        Token keyword = previous();
        consume(SEMICOLON, "Expect ';' after value.");
        return new BreakStatement(keyword);
    }

    private Statement continueStatement() {
        Token keyword = previous();
        consume(SEMICOLON, "Expect ';' after value.");
        return new ContinueStatement(keyword);
    }

    //TODO : improve if to work with multi time of else if before get else
    private Statement ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");
        consume(LEFT_BRACE, "Expect '{' to start if body.");
        List<Statement> thenBranch = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            thenBranch.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' to end if body.");
        List<Statement> elseBranch = null;
        if (match(ELSE)) {
            elseBranch = new ArrayList<>();
            consume(LEFT_BRACE, "Expect '{' to start else body.");
            while (!check(RIGHT_BRACE) && !isAtEnd()) {
                elseBranch.add(declaration());
            }
            consume(RIGHT_BRACE, "Expect '}' to end else body.");
        }
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after while condition.");
        Statement loopBody = statement();
        return new WhileStatement(condition, loopBody);
    }

    private Statement doWhileStatement() {
        Statement loopBody = statement();
        consume(WHILE, "Expect while keyword.");
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after do while condition.");
        consume(SEMICOLON, "Expect ';' after do while condition.");
        return new DoWhileStatement(condition, loopBody);
    }

    private Statement repeatStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'repeat'.");
        Expression value = expression();
        consume(RIGHT_PAREN, "Expect ')' after repeat value.");
        Statement loopBody = statement();
        return new RepeatStatement(value,loopBody);
    }

    private Statement printStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'print'.");
        Expression value = expression();
        consume(RIGHT_PAREN, "Expect ')' after print expression.");
        consume(SEMICOLON, "Expect ';' after value.");
        return new PrintStatement(value);
    }

    private Statement returnStatement() {
        Token keyword = previous();
        Expression value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }
        consume(SEMICOLON, "Expect ';' after return value.");
        return new ReturnStatement(keyword, value);
    }

    private Statement testStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'test statement'.");
        Token name = consume(STRING, "Expect test name.");
        consume(RIGHT_PAREN, "Expect ')' after test statement name value.");
        consume(LEFT_BRACE, "Expect '{' before test body.");
        List<Statement> statements = new ArrayList<>();
        Statement returnStatement = null;
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            Statement statement = declaration();
            if(statement instanceof ReturnStatement) {
                returnStatement = statement;
                consume(RIGHT_BRACE, "Expect '}' after block.");
                break;
            }else{
                statements.add(statement);
            }
        }
        return new TestStatement(name, statements, returnStatement);
    }

    private Statement expressionStatement() {
        Expression expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new ExpressionStatement(expr);
    }

    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        //equality lower than || lower than &&
        Expression expr = elvisExp();

        if (match(EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

            if (expr instanceof Variable) {
                Token name = ((Variable) expr).getName();
                return new AssignExp(name, value);
            }
            else if(expr instanceof ArrayVariable){
                ArrayVariable arrayVariable = ((ArrayVariable) expr);
                Token name = arrayVariable.getName();
                return new ArraySetExp(name, arrayVariable.getIndex(), value);
            }
            else if (expr instanceof GetExp) {
                GetExp get = (GetExp)expr;
                return new SetExp(get.getObject(), get.getName(), value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Expression elvisExp(){
        Expression expr = ternaryExp();
        if (match(ELVIS)) {
            Token elvis = previous();
            Expression rightExp = ternaryExp();
            return  new ElvisExp(expr,elvis,rightExp);
        }
        return expr;
    }

    private Expression ternaryExp(){
        Expression expr = bitwise();

        if (match(QUESTION_MARK)) {
            Token question = previous();
            Expression firstExp = or();
            Token colon = peek();
            if(match(COLON)){
                Expression secondExp = or();
                return new TernaryExp(expr,question,firstExp,colon,secondExp);
            }
            TankRuntime.error(colon,"Expected Expression after COLON");
        }
        return expr;
    }

    private Expression bitwise() {
        Expression expr = or();

        while (match(SHIFT_LEFT, SHIFT_RIGHT, LOGICAL_SHIFT_RIGHT)) {
            Token operator = previous();
            Expression right = xor();
            expr = new BitwiseExp(expr, operator, right);
        }

        return expr;
    }

    private Expression or() {
        Expression expr = xor();

        while (match(OR)) {
            Token operator = previous();
            Expression right = xor();
            expr = new LogicalExp(expr, operator, right);
        }

        return expr;
    }

    private Expression xor() {
        Expression expr = and();

        while (match(XOR)) {
            Token operator = previous();
            Expression right = and();
            expr = new LogicalExp(expr, operator, right);
        }
        return expr;
    }

    private Expression and() {
        Expression expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expression right = equality();
            expr = new LogicalExp(expr, operator, right);
        }
        return expr;
    }

    private Expression equality() {
        Expression expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expr = new BinaryExp(expr, operator, right);
        }
        return expr;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expression comparison() {
        Expression expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expression right = addition();
            expr = new BinaryExp(expr, operator, right);
        }

        return expr;
    }

    private Expression addition() {
        Expression expr = multiplication();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expression right = multiplication();
            expr = new BinaryExp(expr, operator, right);
        }
        return expr;
    }

    private Expression multiplication() {
        Expression expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expression right = unary();
            expr = new BinaryExp(expr, operator, right);
        }
        return expr;
    }

    private Expression unary() {
        if (match(BANG, MINUS, PLUS_PLUS, MINUS_MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new UnaryExp(operator, right);
        }
        return parsePrefixFunctionCall();
    }

    private Expression parsePrefixFunctionCall() {
        if (check(IDENTIFIER) && prefixFunctions.contains(tokens.get(current).lexeme)) {
            current++;
            Token prefixFunctionName = previous();
            Expression right = unary();
            return new PrefixExpression(prefixFunctionName, right);
        }
        return call();
    }

    private Expression call() {
        Expression expr = primary();
        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                expr = new GetExp(name, expr);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        //For zero Arguments
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= MAX_NUM_OF_ARGUMENTS) {
                    //For now just report error but not throws it
                    error(peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
        return new CallExp(callee, paren, arguments);
    }

    private Expression primary() {
        if (match(FALSE)) return new LiteralExp(false);
        if (match(TRUE)) return new LiteralExp(true);
        if (match(NIL)) return new LiteralExp(null);
        if (match(THIS)) return new ThisExp(previous());
        if (match(NUMBER, STRING, CHAR)) return new LiteralExp(previous().literal);
        if (match(IDENTIFIER)) {
            Token next = peek();
            if(next.type == ARRAY_OPEN){
                Token name = previous();
                consume(ARRAY_OPEN, "Expect [");
                Expression index = expression();
                consume(ARRAY_CLOSE, "Expect ]");
                return new ArrayVariable(name, index);
            }
            return new Variable(previous());
        }
        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new GroupingExp(expr);
        }
        if (match(ARRAY)){
            consume(ARRAY_OPEN, "Expect [");
            Expression size = expression();
            consume(ARRAY_CLOSE, "Expect ]");
            return new ArrayGetExp(size);
        }
        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER,"Expect superclass method name.");
            return new SuperExp(keyword, method);
        }
        throw error(peek(), "Expect expression. " + tokens.get(current));
    }

    private ParseError error(Token token, String message) {
        TankRuntime.error(token.line, message);
        return new ParseError();
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }
}

package parser;

import ast.*;
import runtime.TankRuntime;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.List;

import static token.TokenType.*;

public class Parser {

    /*
     * TODO : Create advanced error system for parser to show expect .... in line ....
     */

    private int current = 0;
    private final List<Token> tokens;

    private static final int MAX_NUM_OF_ARGUMENTS = 8;

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
        //TODO : if match [ return new ArrayStatement
        return expressionStatement();
    }

    private Statement declaration() {
        try {
            if (match(FUN)) return funcDeclaration("function");
            if (match(VAR)) return varDeclaration();
            if (match(CLASS)) return classDeclaration();
            //TODO : support class, list and map --> maybe struct :D
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
            methods.add(methodDeclaration("method"));
        }

        consume(RIGHT_BRACE, "Expect '}' after class body.");

        return new ClassStatement(name, superclass, methods);
    }

    private FunctionStatement methodDeclaration(String kind) {
        consume(FUN, "Expect func keyword");
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
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
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Statement> body = block();
        return new FunctionStatement(name, parameters, body);
    }

    private FunctionStatement funcDeclaration(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
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
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Statement> body = block();
        return new FunctionStatement(name, parameters, body);
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
        consume(SEMICOLON, "Expect ';' after value.");
        return new BreakStatement();
    }

    private Statement continueStatement() {
        consume(SEMICOLON, "Expect ';' after value.");
        return new ContinueStatement();
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
        consume(LEFT_BRACE, "Expect '{' to start while body.");
        List<Statement> loopBodyStmts = block();
        return new WhileStatement(condition, loopBodyStmts);
    }

    private Statement doWhileStatement() {
        consume(LEFT_BRACE, "Expect '{' to start do while body.");
        List<Statement> loopBodyStmts = block();
        consume(WHILE, "Expect while keyword.");
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after while condition.");
        consume(SEMICOLON, "Expect ';' after do while condition.");
        return new DoWhileStatement(condition, loopBodyStmts);
    }

    private Statement repeatStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'repeat'.");
        Expression value = expression();
        consume(RIGHT_PAREN, "Expect ')' after repeat value.");
        consume(LEFT_BRACE, "Expect '{' to start do while body.");
        List<Statement> repeatStatementList = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            repeatStatementList.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' to end do while body.");
        return new RepeatStatement(value,repeatStatementList);
    }

    private Statement printStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression value = expression();
        consume(RIGHT_PAREN, "Expect ')' after while condition.");
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
        Expression expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

            if (expr instanceof Variable) {
                Token name = ((Variable) expr).getName();
                return new AssignExp(name, value);
            }
            else if (expr instanceof GetExp) {
                GetExp get = (GetExp)expr;
                return new SetExp(get.getObject(), get.getName(), value);
            }

            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Expression or() {
        Expression expr = xor();

        while (match(OR)) {
            Token operator = previous();
            Expression right = and();
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
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new UnaryExp(operator, right);
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
        if (match(IDENTIFIER)) return new Variable(previous());
        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new GroupingExp(expr);
        }
        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER,"Expect superclass method name.");
            return new SuperExp(keyword, method);
        }
        throw error(peek(), "Expect expression.");
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

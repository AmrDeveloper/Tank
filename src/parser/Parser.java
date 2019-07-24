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

    private Statement declaration() {
        try {
            if (match(VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
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

    private Statement statement() {
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(PRINT)) return printStatement();
        if (match(LEFT_BRACE)) return new BlockStatement(block());
        if (match(BREAK)) return breakStatement();
        if (match(CONTINUE)) return continueStatement();
        //TODO : if match [ return new ArrayStatement
        return expressionStatement();
    }

    private Statement breakStatement(){
        consume(SEMICOLON, "Expect ';' after value.");
        return new BreakStatement();
    }

    private Statement continueStatement(){
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

    private Statement whileStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after while condition.");
        consume(LEFT_BRACE, "Expect '{' to start while body.");
        List<Statement> loopBodyStmts = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            loopBodyStmts.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' to end while body.");
        return new WhileStatement(condition,loopBodyStmts);
    }

    private Statement printStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression value = expression();
        consume(RIGHT_PAREN, "Expect ')' after while condition.");
        consume(SEMICOLON, "Expect ';' after value.");
        return new PrintStatement(value);
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

    private Expression xor(){
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
        return primary();
    }

    private Expression primary() {
        if (match(FALSE)) return new LiteralExp(false);
        if (match(TRUE)) return new LiteralExp(true);
        if (match(NIL)) return new LiteralExp(null);

        if (match(NUMBER, STRING, CHAR)) {
            return new LiteralExp(previous().literal);
        }

        if (match(IDENTIFIER)) {
            return new Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new GroupingExp(expr);
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

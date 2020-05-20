package lexer;

import runtime.TankRuntime;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static token.TokenType.*;

public class TankLexer {

    private String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;

    private int start = 0;
    private int current = 0;
    private int line = 1;

    static {
        keywords = new HashMap<>();
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("func", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("and", AND);
        keywords.put("or", OR);
        keywords.put("xor", XOR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("do", DO);
        keywords.put("while", WHILE);
        keywords.put("repeat", REPEAT);
        keywords.put("break", BREAK);
        keywords.put("continue", CONTINUE);
        keywords.put("extends", EXTENDS);
        keywords.put("array", ARRAY);
        keywords.put("module", MODULE);
    }

    public TankLexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = pointToNextChar();
        switch (c) {
            case '[':
                addToken(ARRAY_OPEN);
                break;
            case ']':
                addToken(ARRAY_CLOSE);
                break;
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(match('-') ? MINUS_MINUS : MINUS);
                break;
            case '+':
                addToken(match('+') ? PLUS_PLUS : PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '?':
                addToken(match(':') ? ELVIS : QUESTION_MARK);
                break;
            case ':':
                addToken(COLON);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (getCurrentChar() != '\n' && !isAtEnd()) pointToNextChar();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"':
                scanString();
                break;
            case '\'':
                scanChar();
                break;
            default:
                if (isDigit(c)) {
                    scanNumber();
                } else if (isAlpha(c)) {
                    scanIdentifier();
                } else {
                    TankRuntime.error(line, "Unexpected character.");
                }
        }
    }

    private void scanNumber() {
        while (isDigit(getCurrentChar())) pointToNextChar();

        // Look for a fractional part.
        if (getCurrentChar() == '.' && isDigit(getNextChar())) {
            // Consume the "."
            pointToNextChar();
            while (isDigit(getCurrentChar())) pointToNextChar();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void scanString() {
        while (getCurrentChar() != '"' && !isAtEnd()) {
            if (getCurrentChar() == '\n') line++;
            pointToNextChar();
        }

        // Unterminated scanString.
        if (isAtEnd()) {
            TankRuntime.error(line, "Unterminated scanString.");
            return;
        }

        // The closing ".
        pointToNextChar();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void scanChar() {
        String value = source.substring(start + 1, start + 2);
        pointToNextChar();
        if (getCurrentChar() != '\'') {
            TankRuntime.error(line, "Unterminated char variable.");
            return;
        }
        pointToNextChar();
        addToken(CHAR, value.charAt(0));
    }

    private void scanIdentifier() {
        while (isAlphaNumeric(getCurrentChar())) pointToNextChar();

        // See if the scanIdentifier is a reserved word.
        String text = source.substring(start, current);

        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private char pointToNextChar() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char getCurrentChar() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char getNextChar() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}

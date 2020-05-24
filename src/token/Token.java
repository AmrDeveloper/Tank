package token;

public class Token {

    public final TokenType type;
    public String lexeme;
    public Object literal;
    public final int line;

    public Token(TokenType type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = null;
        this.line = line;
    }

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public void concat(Token token) {
        if(token == null) {
            return;
        }

        this.lexeme += token.lexeme;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}

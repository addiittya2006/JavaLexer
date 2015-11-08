package org.addiittya.lexer;

public class Token {
    private final String text;
    private final TokenType type; // 0 - unknown

    public Token(String text, TokenType type) {
        this.text = text;
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        return !(text != null ? !text.equals(token.text) : token.text != null);

    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    @Override
    public String toString() {
        return text;
    }
}

enum TokenType {
    //Recognised and UNRecognized Tokens Categorization
    UNREC, REC
}
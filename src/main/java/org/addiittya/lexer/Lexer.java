package org.addiittya.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer implements Iterable<Token> {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private StringBuilder buffer = new StringBuilder("");
    private final Pattern pattern;
    private InputStream source;
    private Lexer lexer;

    public Lexer(InputStream source, String matchingString) {
        this(matchingString);
        this.source = source;
    }

    public Lexer(Lexer lexer, String matchingString) {
        this(matchingString);
        this.lexer = lexer;
    }

    public Token readToken() {
        if (source != null) return readTokenFromStream();
        else return readTokenFromLexer();
    }

    private Token readTokenFromStream() {
        try {
            while (tokens.isEmpty()) {
                int ch;
                if ((ch = source.read()) != -1) {
                    buffer.append((char) ch);
                    Matcher matcher = pattern.matcher(buffer);
                    if (matcher.find()) {
                        if (matcher.end() < buffer.length()) {
                            tokens.add(new Token(buffer.substring(0, matcher.start()), TokenType.UNREC));
                            tokens.add(new Token(buffer.substring(matcher.start(), matcher.end()), TokenType.REC));
                            buffer.delete(0, matcher.end());
                        }
                    }
                }
                if (ch == -1) {
                    tokens.add(new Token(buffer.toString(), TokenType.UNREC));
                    buffer = new StringBuilder("");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        removeEmptyTokens();
        if (!tokens.isEmpty())
            return tokens.remove(0);
        return null;
    }

    private Token readTokenFromLexer() {
        Token token;
        Iterator<Token> iterator = lexer.iterator();

        while (tokens.isEmpty()) {
            if (iterator.hasNext())
                token = iterator.next();
            else
                return null;

            if (token.getType() == TokenType.REC) return token;

            String text = token.toString();
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                tokens.add(new Token(text.substring(0, matcher.start()), TokenType.UNREC));
                tokens.add(new Token(text.substring(matcher.start(), matcher.end()), TokenType.REC));
                tokens.add(new Token(text.substring(matcher.end(), text.length()), TokenType.UNREC));
            } else {
                tokens.add(new Token(text, TokenType.UNREC));
            }
        }

        removeEmptyTokens();
        if (!tokens.isEmpty())
            return tokens.remove(0);
        return null;
    }

    @Override
    public Iterator<Token> iterator() {
        return new LexerIterator();
    }

    private Lexer(String matchingString) {
        pattern = Pattern.compile(matchingString);
    }

    private void removeEmptyTokens() {
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            Token object = iterator.next();
            String text = object.toString();
            if (text.equals("") || text.equals(" "))
                iterator.remove();
        }
    }

    private class LexerIterator implements Iterator<Token> {

        private Token token = null;
        private boolean read = false;

        @Override
        public boolean hasNext() {
            token = Lexer.this.readToken();
            read = true;
            return token != null;
        }

        @Override
        public Token next() {
            if (read) {
                read = false;
                return checkTokenValueAndReturn();
            } else {
                token = Lexer.this.readToken();
                return checkTokenValueAndReturn();
            }
        }

        private Token checkTokenValueAndReturn() {
            if (token != null)
                return token;
            else
                throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}

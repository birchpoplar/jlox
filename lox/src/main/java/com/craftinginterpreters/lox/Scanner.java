package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;      // start of the current lexeme
    private int current = 0;    // current character being considered
    private int line = 1;       // current line in the source code
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }
    
    Scanner(String source) {
        this.source = source;   
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        
        // Add an end-of-file token after scanning available tokens.
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
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
                    // A line comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    // System.out.println("Starting block comment");
                    // Keep going until we find */ or reach end of file
                    while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
                        if (peek() == '\n') line++; // Track line numbers in block comments
                        advance();
                    }
                    if (!isAtEnd()) {
                        // Consume the closing */
                        advance(); // consume '*'
                        advance(); // consume '/'
                        // System.out.println("Ending block comment");
                    } else {
                        Lox.error(line, "Unterminated block comment.");
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
                
            case '\n':
                line++;
                break;

            case '"': string(); break;

            default:
                if(isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        // Consume characters as long as they are digits.
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        // Convert the lexeme to a number and add it as a token
        addToken(NUMBER, 
            Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        // Consume characters until we find the closing quote or reach the end of the source.
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // Consume the closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        // Check if the current character matches the expected character.
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        // Only consume character if it's a match.
        current++;
        return true;
    }   

    private char peek() {
        // Look at the current character without consuming it.
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        // Look at the next character without consuming it.
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        // Check if the character is an alphabetic character or underscore.
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        // Check if the character is alphanumeric (letter or digit) or underscore.
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        // Check if the character is a digit.
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        // Check if we've consumed all characters in the source.
        return current >= source.length();
    } 
    
    private char advance() {
        // Consume the current character and return it.
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}   

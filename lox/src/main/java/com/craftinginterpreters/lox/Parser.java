package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;
import static com.craftinginterpreters.lox.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;
    
    public static void setMonitorLevel(int level) {
        ParserMonitor.setMonitorLevel(level);
    }

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new java.util.ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Expr expression() {
        ParserMonitor.enterRule("expression", peek(), current);
        Expr result = equality();
        ParserMonitor.exitRule("expression", result != null);
        return result;
    }

    private Stmt declaration() {
        ParserMonitor.enterRule("declaration", peek(), current);
        try {
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt statement() {
        if (match(PRINT)) return printStatement();

        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr equality() {
        ParserMonitor.enterRule("equality", peek(), current);
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        ParserMonitor.exitRule("equality", expr != null);
        return expr;
    }

    private Expr comparison() {
        ParserMonitor.enterRule("comparison", peek(), current);
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        ParserMonitor.exitRule("comparison", expr != null);
        return expr;
    }

    private Expr term() {
        ParserMonitor.enterRule("term", peek(), current);
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        ParserMonitor.exitRule("term", expr != null);
        return expr;
    }

    private Expr factor() {
        ParserMonitor.enterRule("factor", peek(), current);
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        ParserMonitor.exitRule("factor", expr != null);
        return expr;
    }

    private Expr unary() {
        ParserMonitor.enterRule("unary", peek(), current);
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            ParserMonitor.exitRule("unary", right != null);
            return new Expr.Unary(operator, right);
        }

        Expr result = primary();
        ParserMonitor.exitRule("unary", result != null);
        return result;
    }

    private Expr primary() {
        ParserMonitor.enterRule("primary", peek(), current);
        if (match(FALSE)) {
            ParserMonitor.exitRule("primary", true);
            return new Expr.Literal(false);
        }
        if (match(TRUE)) {
            ParserMonitor.exitRule("primary", true);
            return new Expr.Literal(true);
        }
        if (match(NIL)) {
            ParserMonitor.exitRule("primary", true);
            return new Expr.Literal(null);
        }

        if (match(NUMBER, STRING)) {
            ParserMonitor.exitRule("primary", true);
            return new Expr.Literal(previous().literal);
        }

        if (match(IDENTIFIER)) {
            ParserMonitor.exitRule("primary", true);
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            ParserMonitor.exitRule("primary", expr != null);
            return new Expr.Grouping(expr);
        }

        ParserMonitor.exitRule("primary", false);
        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        ParserMonitor.matchAttempt(types, peek(), current);
        for (TokenType type : types) {
            if (check(type)) {
                Token matched = peek();
                advance();
                ParserMonitor.matchResult(true, matched);
                return true;
            }
        }

        ParserMonitor.matchResult(false, null);
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            Token from = peek();
            current++;
            ParserMonitor.advance(from, peek(), current);
        }
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
    
    private ParseError error(Token token, String message) {
        ParserMonitor.parseError(token, message);
        Lox.error(token, message);
        return new ParseError();
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

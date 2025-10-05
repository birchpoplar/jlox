package com.craftinginterpreters.lox;

import java.util.List;

class ParserMonitor {
    public static final int MONITOR_OFF = 0;
    public static final int MONITOR_MINIMAL = 1;
    public static final int MONITOR_VERBOSE = 2;
    public static final int MONITOR_VERY_VERBOSE = 3;
    
    private static int monitorLevel = MONITOR_OFF;
    
    public static void setMonitorLevel(int level) {
        monitorLevel = level;
    }
    
    public static int getMonitorLevel() {
        return monitorLevel;
    }
    
    public static void enterRule(String ruleName, Token currentToken, int position) {
        if (monitorLevel >= MONITOR_MINIMAL) {
            System.out.printf("→ %s", ruleName);
            if (monitorLevel >= MONITOR_VERBOSE) {
                System.out.printf(" [%d:%s]", position, currentToken.type);
                if (monitorLevel >= MONITOR_VERY_VERBOSE) {
                    System.out.printf(" '%s'", currentToken.lexeme);
                }
            }
            System.out.println();
        }
    }
    
    public static void exitRule(String ruleName, boolean success) {
        if (monitorLevel >= MONITOR_MINIMAL) {
            System.out.printf("← %s", ruleName);
            if (monitorLevel >= MONITOR_VERBOSE) {
                System.out.printf(" (%s)", success ? "✓" : "✗");
            }
            System.out.println();
        }
    }
    
    public static void matchAttempt(TokenType[] types, Token currentToken, int position) {
        if (monitorLevel >= MONITOR_VERBOSE) {
            System.out.printf("  ? match");
            if (monitorLevel >= MONITOR_VERY_VERBOSE) {
                System.out.print(" [");
                for (int i = 0; i < types.length; i++) {
                    if (i > 0) System.out.print(",");
                    System.out.print(types[i]);
                }
                System.out.printf("] vs %s", currentToken.type);
            }
        }
    }
    
    public static void matchResult(boolean matched, Token token) {
        if (monitorLevel >= MONITOR_VERBOSE) {
            System.out.printf(" → %s", matched ? "✓" : "✗");
            if (matched && monitorLevel >= MONITOR_VERY_VERBOSE) {
                System.out.printf(" consumed '%s'", token.lexeme);
            }
            System.out.println();
        }
    }
    
    public static void advance(Token from, Token to, int newPosition) {
        if (monitorLevel >= MONITOR_VERY_VERBOSE) {
            System.out.printf("  advance: %s → %s [pos %d]\n", 
                from.type, to.type, newPosition);
        }
    }
    
    public static void parseError(Token token, String message) {
        if (monitorLevel >= MONITOR_MINIMAL) {
            System.out.printf("! ERROR at %s: %s\n", token.type, message);
        }
    }
    
    public static void showTokens(List<Token> tokens, int currentPosition) {
        if (monitorLevel >= MONITOR_VERY_VERBOSE) {
            System.out.print("  tokens: ");
            for (int i = 0; i < tokens.size() && i < currentPosition + 3; i++) {
                if (i == currentPosition) System.out.print("[");
                System.out.print(tokens.get(i).type);
                if (i == currentPosition) System.out.print("]");
                if (i < tokens.size() - 1 && i < currentPosition + 2) System.out.print(" ");
            }
            System.out.println();
        }
    }
}
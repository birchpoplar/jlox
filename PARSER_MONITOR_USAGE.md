# Parser Monitor Usage Guide

The parser monitoring system has been added to help you understand how the parser operates. Here's how to use it:

## Setting Monitor Levels

Add this line to your main code before parsing:

```java
Parser.setMonitorLevel(ParserMonitor.MONITOR_MINIMAL);    // Level 1: Basic rule entry/exit
Parser.setMonitorLevel(ParserMonitor.MONITOR_VERBOSE);    // Level 2: + token matching details
Parser.setMonitorLevel(ParserMonitor.MONITOR_VERY_VERBOSE); // Level 3: + token content and advances
```

## Monitor Levels Explained

### MONITOR_MINIMAL (Level 1)
Shows basic parser rule entry and exit:
```
→ expression
→ equality
→ comparison
...
← comparison
← equality
← expression
```

### MONITOR_VERBOSE (Level 2)
Adds token position, type, and match attempts:
```
→ expression [0:NUMBER]
→ equality [0:NUMBER]
  ? match → ✗
← equality (✓)
← expression (✓)
```

### MONITOR_VERY_VERBOSE (Level 3)
Shows full token content, advances, and token stream:
```
  tokens: [NUMBER] PLUS EOF
→ expression [0:NUMBER] '123'
→ equality [0:NUMBER] '123'
  ? match [BANG_EQUAL,EQUAL_EQUAL] vs NUMBER → ✗
  advance: NUMBER → PLUS [pos 1]
← equality (✓)
← expression (✓)
```

## Example Usage in Lox.java

```java
// In your main parsing method, add:
if (args.length > 0 && args[0].equals("--debug")) {
    Parser.setMonitorLevel(ParserMonitor.MONITOR_VERBOSE);
}

// Then parse as normal
Parser parser = new Parser(tokens);
Expr expression = parser.parse();
```

This will help you see exactly how the recursive descent parser processes your input tokens step by step.
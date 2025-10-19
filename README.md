# jlox - Java Tree-Walk Interpreter

A tree-walking interpreter for the Lox programming language, implemented in Java as part of my journey through [Crafting Interpreters](https://craftinginterpreters.com/) by Robert Nystrom.

This implementation follows **Part II** of the book, building a complete interpreter using the visitor pattern to traverse an Abstract Syntax Tree (AST).

## Features

- ✅ **Variables and Scoping** - Local and global variable declarations with proper lexical scoping
- ✅ **Control Flow** - `if/else` statements, `while` and `for` loops
- ✅ **Functions** - First-class functions with closures and recursion
- ✅ **Classes** - Object-oriented programming with inheritance and method calls
- ✅ **Static Analysis** - Resolver phase for optimized variable access and error detection
- ✅ **Error Handling** - Comprehensive syntax and runtime error reporting

## Language Examples

```lox
// Variables and basic expressions
var greeting = "Hello, " + "world!";
print greeting;

// Functions with closures
fun makeCounter() {
    var i = 0;
    fun count() {
        i = i + 1;
        print i;
    }
    return count;
}

var counter = makeCounter();
counter(); // 1
counter(); // 2

// Classes and inheritance
class Animal {
    speak() {
        print "Some generic animal sound";
    }
}

class Dog < Animal {
    speak() {
        print "Woof!";
    }
}

var dog = Dog();
dog.speak(); // "Woof!"
```

## Running the Interpreter

**Compile:**
```bash
cd lox
javac -d target/classes src/main/java/com/craftinginterpreters/lox/*.java
```

**Run a Lox file:**
```bash
java -cp target/classes com.craftinginterpreters.lox.Lox script.lox
```

**Interactive REPL:**
```bash
java -cp target/classes com.craftinginterpreters.lox.Lox
```

## Architecture

- **Scanner** - Tokenizes source code into a stream of tokens
- **Parser** - Builds an Abstract Syntax Tree using recursive descent parsing
- **Resolver** - Static analysis pass that resolves variable bindings and catches scoping errors
- **Interpreter** - Tree-walking interpreter using the visitor pattern for AST traversal

## About Crafting Interpreters

[Crafting Interpreters](https://craftinginterpreters.com/) is an excellent book that teaches you how to build programming languages from scratch. The book is split into two parts:

- **Part I (jlox)** - A tree-walking interpreter in Java (this implementation)
- **Part II (clox)** - A bytecode virtual machine in C

## Acknowledgments

All credit for the language design and implementation approach goes to [Robert Nystrom](https://github.com/munificent) and his fantastic book [Crafting Interpreters](https://craftinginterpreters.com/).

This repository represents my personal journey through Part II of the book, implementing each chapter's concepts and features.
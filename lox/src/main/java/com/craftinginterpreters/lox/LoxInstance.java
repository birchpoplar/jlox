package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
    private LoxClass klass;
    private Map<String, Object> fields = new HashMap<>();
    
    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }   

    Object get(Token name) {
        if (fields.containsKey(name)) {
            return fields.get(name);
        }

        LoxFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(null, "Undefined property '" + name + "'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}

package jlox.interpreters.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();
    
    public Environment() {
        enclosing = null;
    }
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }
    void define(String name, Object value) {
        values.put(name, value);
    }
    /**
     * 
     * @param name Token
     * @return value in Environment or enclosing environments
     * @throws RuntimeError if variable is undefined
     */
    Object get(Token name) throws RuntimeError {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        if (enclosing != null) {
            return enclosing.get(name);
        }
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
    /**
     * 
     * @param name
     * @param value
     * @throws RuntimeError if variable is undefined
     */
    void assign(Token name, Object value) throws RuntimeError {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
    
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }
    
    private Environment ancestor(final int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }
    public void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
        
    }
}

package jlox.interpreters.lox;

import java.util.HashMap;
import java.util.Map;

/**
 * runtime representation of an instance of a Lox class
 *
 */
public class LoxInstance {
    
    private final LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();
    
    public LoxInstance(LoxClass klass) {
        this.klass = klass;
    }
    
    Object get(Token name) throws RuntimeError {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }
        
        LoxFunction method = klass.findMethod(this, name.lexeme);
        if (method != null) {
            return method;
        }
        throw new RuntimeError(name,  "Undefined property '" + name.lexeme + "'.");
    }
    
    @Override                          
    public String toString() {         
      return klass.name + " instance"; 
    }

    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

}

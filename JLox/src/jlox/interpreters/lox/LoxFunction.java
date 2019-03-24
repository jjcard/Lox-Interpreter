package jlox.interpreters.lox;

import java.util.List;

import jlox.interpreters.lox.Propogator.ReturnPropogator;

/**
 * Runtime wrapper for Stmt.Function that implements LoxCallable
 *
 */
public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;
    
    LoxFunction(Stmt.Function declaration, Environment closure) {
        this(declaration, closure, false);
    }
    LoxFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
      this.declaration = declaration;       
      this.closure = closure;
      this.isInitializer = isInitializer;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        final Environment environment = new Environment(closure);

        for (int i = 0; i < arity(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnPropogator returnValue) {
            if (isInitializer) {
                assert returnValue.value == null: "explicit return value should always be nil for initializers";
                //Forcibly return 'this' for initializer method
                return closure.getAt(0, "this");
            }
            return returnValue.value;
        }
        if (isInitializer) {
            //Forcibly return 'this' for initializer method
            return closure.getAt(0, "this");
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }
    @Override                                       
    public String toString() {
      return "<fn " + declaration.name.lexeme + ">";
    }
    public LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, isInitializer);
    } 

}

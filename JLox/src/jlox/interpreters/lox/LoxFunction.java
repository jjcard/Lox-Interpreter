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
    LoxFunction(Stmt.Function declaration, Environment closure) {
      this.declaration = declaration;       
      this.closure = closure;
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
       return returnValue.value;
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

}

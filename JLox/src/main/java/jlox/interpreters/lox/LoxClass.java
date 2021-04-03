package jlox.interpreters.lox;

import java.util.List;
import java.util.Map;

import jlox.interpreters.lox.Stmt.Function;

public class LoxClass implements LoxCallable {
    /** special name for the initializer method*/
    private static final String INIT_METHOD_NAME = "init";
    final String name;
    final LoxClass superclass;
    private final Map<String, LoxFunction> methods;
    
    public LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }
    
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = getInitializer();
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity() {
        //of the constructor
        LoxFunction initializer = getInitializer();
        if (initializer == null) {
            return 0;
        }
        return initializer.arity();
    }

    private LoxFunction getInitializer() {
        return methods.get(INIT_METHOD_NAME);
    }

    public LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }
        if (superclass != null) {
            return superclass.findMethod(name);
        }
        return null;
    }
    public static boolean isInitializer(Function function) {
        return function != null && INIT_METHOD_NAME.equals(function.name.lexeme);
    }

}

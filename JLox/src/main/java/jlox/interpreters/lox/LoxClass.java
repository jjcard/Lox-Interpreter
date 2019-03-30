package jlox.interpreters.lox;

import java.util.List;
import java.util.Map;

import jlox.interpreters.lox.Stmt.Function;

public class LoxClass implements LoxCallable{
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
        LoxFunction initilizer = getInitializer();
        if (initilizer != null) {
            initilizer.bind(instance).call(interpreter, arguments);
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
        return methods.get("init");
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
    public static boolean isInitializer(Function funcition) {
        return funcition != null && "init".equals(funcition.name.lexeme);
    }

}

package jlox.interpreters.lox;

import java.util.List;

public interface LoxCallable {

    Object call(Interpreter interpreter, List<Object> arguments);

    /**
     *
     * @return arity the number of arguments Callable expects
     */
    int arity();
}

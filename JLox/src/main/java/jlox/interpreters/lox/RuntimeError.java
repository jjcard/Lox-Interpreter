package jlox.interpreters.lox;

@SuppressWarnings("serial")
public class RuntimeError extends RuntimeException {

    final Token token;
    
    public RuntimeError(Token token, final String message) {
        super(message);
        this.token = token;
    }
}

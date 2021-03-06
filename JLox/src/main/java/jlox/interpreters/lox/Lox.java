package jlox.interpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    private static boolean testingMode = false;
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;

    private static PrintStream out = System.out;
    private static PrintStream err = System.err;
    
    
    private static final Interpreter interpreter = new Interpreter();
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }

    }
    
    protected static void clearState() {
        hadError = false;
        hadRuntimeError = false;
    }
    protected static void setOut(PrintStream stream) {
        out = stream;
    }
    protected static void setErr(PrintStream stream) {
        err = stream;
    }
    /**
     * Turn on/off testing mode. Testing mode on means will not System exit with error code if unable to parse.
     * @param testingMode
     */
    protected static void setTestingMode(boolean testingMode) {
        Lox.testingMode = testingMode;
    }
    private static void runPrompt() throws IOException {
        final InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        
        while(true) {
            out.println("> ");
            run(reader.readLine());
            hadError = false;
        }
        
    }

    protected static void run(String source) {
        final LoxScanner scanner = new LoxScanner(source);
        final List<Token> tokens = scanner.scanTokens();

        final Parser parser = new Parser(tokens);
        final List<Stmt> statements = parser.parse();
        //stop if there was a syntax error
        if (hadError) {
            return;
        }
        
        final Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        
        //Stop if there was a resolution error
        if (hadError) {
            return;
        }
        
        interpreter.interpret(statements);
        
    }

    protected static void runFile(final String path) throws IOException {
        final byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        
        if (!testingMode) {
            // Indicate an error in the exit code.
            if (hadError) {
                System.exit(65);
            }
            if (hadRuntimeError) {
                System.exit(79);
            }
        }

    }
    static void error(int line, String message) {
        report(line, "", message);
    }
    static void runtimeError(RuntimeError error) {
        err.println(error.getMessage() +     
            "\n[line " + error.token.line + "]");   
        hadRuntimeError = true;                     
      }
    private static void report(int line, String where, String message) {
        err.println("[line " + line + "] Error" + where + ": " + message);        
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
    static PrintStream getOut() {
        return out;
    }

}

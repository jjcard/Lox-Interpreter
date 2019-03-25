package jlox.interpreters.lox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LoxTest {
    private static String NEW_LINE = System.getProperty("line.separator");

    private static PrintStream defaultOut;
    private static PrintStream defaultErrorOut;
    
    private static ByteArrayOutputStream testOut;
    private static ByteArrayOutputStream testErrorOut;
    
    @BeforeAll
    public static void beforeClass() throws IOException {
        defaultOut = System.out;
        defaultErrorOut = System.err;
        
        testOut = new ByteArrayOutputStream();
        testErrorOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        System.setErr(new PrintStream(testErrorOut));
        
    }
    
    @AfterAll
    public static void afterClass() throws IOException {
        System.setOut(defaultOut);
        System.setErr(defaultErrorOut);
    }
    
    @Test
    void printString() throws IOException {
        Lox.run("print \"Hello World\";");
        
        String output = testOut.toString();
        assertEquals("", testErrorOut.toString(), "Should not have errors");
        
        assertEquals("Hello World" +NEW_LINE, output);
    }



}

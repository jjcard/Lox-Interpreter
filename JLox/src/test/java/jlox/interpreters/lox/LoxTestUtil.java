package jlox.interpreters.lox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class LoxTestUtil {
    private static String NEW_LINE = System.getProperty("line.separator");
    private static PrintStream defaultOut;
    private static PrintStream defaultErrorOut;
    
    private static ByteArrayOutputStream testOut;
    private static ByteArrayOutputStream testErrorOut;
    
    
    public static void beforeClass() throws IOException {
        defaultOut = System.out;
        defaultErrorOut = System.err;
        
        testOut = new ByteArrayOutputStream();
        testErrorOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        System.setErr(new PrintStream(testErrorOut));
        
    }
    
    public static void afterEach() {
        testOut.reset();
        testErrorOut.reset();
    }
    
    public static void afterClass() throws IOException {
        System.setOut(defaultOut);
        System.setErr(defaultErrorOut);
    }
    
    public static void assertHasNoErrors() {
        assertEquals("", testErrorOut.toString(), "Should not have errors");
    }
    public static void assertLineEquals(String expected) {
        String output = testOut.toString();
        assertEquals(expected +NEW_LINE, output);

    }
    public static void assertLinesEquals(String... expected) {
        String output = testOut.toString();
        String expectedCombined = String.join(NEW_LINE, expected);
        assertEquals(expectedCombined, output);

    }
}

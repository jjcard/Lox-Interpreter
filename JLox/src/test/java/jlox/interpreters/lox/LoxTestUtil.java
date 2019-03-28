package jlox.interpreters.lox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class LoxTestUtil {
    private static String NEW_LINE = System.getProperty("line.separator");

    
    private static ByteArrayOutputStream testOut;
    private static ByteArrayOutputStream testErrorOut;
    
    
    public static void beforeClass() throws IOException {
        
        testOut = new ByteArrayOutputStream();
        testErrorOut = new ByteArrayOutputStream();
        Lox.setOut(new PrintStream(testOut));
        Lox.setErr(new PrintStream(testErrorOut));
        
    }
    
    public static void afterEach() {
        testOut.reset();
        testErrorOut.reset();
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
        String expectedCombined = String.join(NEW_LINE, expected) + NEW_LINE;
        assertEquals(expectedCombined, output);

    }
}

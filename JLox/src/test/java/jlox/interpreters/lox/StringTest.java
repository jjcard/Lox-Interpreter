package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class StringTest extends BaseLoxTest {
    
    private static final String STRING_TEST_FILE_DIR = TEST_FILE_DIR + "string/";

    
    //*****From Test Suite
    @Test
    void errorAfterMultiline() throws IOException {
        Lox.runFile(STRING_TEST_FILE_DIR + "error_after_multiline.lox");
        LoxTestUtil.assertRuntimeError("Undefined variable 'err'.", 7);
    }
    @Test
    void literals() throws IOException {
        Lox.runFile(STRING_TEST_FILE_DIR + "literals.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("()", "a string", "A~¶Þॐஃ");
    }
    @Test
    void multiline() throws IOException {
        Lox.runFile(STRING_TEST_FILE_DIR + "multiline.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("1","2","3");
    }
    
    @Test
    void unterminated() throws IOException {
        Lox.runFile(STRING_TEST_FILE_DIR + "unterminated.lox");
        LoxTestUtil.assertErrorLineEquals("[line 2] Error: Unterminated string.");
    }
}

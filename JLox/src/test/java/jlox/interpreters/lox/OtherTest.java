package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;
import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
/**
 * For tests that don't have enough to have their own test class
 *
 */
class OtherTest extends BaseLoxTest {

    //****From Test Suite (done)
    @Test
    void emptyFile() throws IOException {
        Lox.runFile(TEST_FILE_DIR + "empty_file.lox");
        LoxTestUtil.assertHasNoErrors();
        LoxTestUtil.assertNoLines();
    }
    @Test
    void precedence() throws IOException {
        Lox.runFile(TEST_FILE_DIR + "precedence.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("14", "8", "4", "0", "true", "true", "true", "true", "0", "0", "0", "0", "4");
    }
    
    @Test
    void unexpectedCharacter() throws IOException {
        Lox.runFile(TEST_FILE_DIR + "unexpected_character.lox");
        LoxTestUtil.assertErrorLineEquals("[line 3] Error: Unexpected character.\r\n" + 
                "[line 3] Error at 'b': Expect ')' after arguments.");
    }
    
    //regression
    
    @Test
    void regression_394() throws IOException {
        Lox.runFile(TEST_FILE_DIR + "/regression/394.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("B");
    }
    @Test
    void regression_40() throws IOException {
        Lox.runFile(TEST_FILE_DIR + "/regression/40.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("false");
    }

}

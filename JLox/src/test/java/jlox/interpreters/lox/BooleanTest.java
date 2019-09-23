package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class BooleanTest extends BaseLoxTest {
    private static final String BOOL_TEST_FILE_DIR = TEST_FILE_DIR + "bool/";
    
    //*** From Test Suite (done)
    @Test
    void equality() throws IOException {
        Lox.runFile(BOOL_TEST_FILE_DIR + "equality.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("true", "false", "false", "true",
                /*Not = to other types*/
                "false","false","false","false","false",
                /*!=*/
                "false", "true", "true", "false",
                /*!= to other types*/
                "true","true","true","true","true");
    }
    @Test
    void not() throws IOException {
        Lox.runFile(BOOL_TEST_FILE_DIR + "not.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("false", "true", "true");
    }

}

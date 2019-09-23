package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class NilTest extends BaseLoxTest {
    private static final String NIL_TEST_FILE_DIR = TEST_FILE_DIR + "nil/";
    
    //** From Test Suite (done)
    @Test
    void test() throws IOException {
        Lox.runFile(NIL_TEST_FILE_DIR + "literal.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("nil");
    }

}

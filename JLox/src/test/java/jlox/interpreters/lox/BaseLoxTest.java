package jlox.interpreters.lox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

class BaseLoxTest {

    public static final String TEST_FILE_DIR = "../test/";
    @BeforeAll
    public static void beforeClass() {
        Lox.clearState();
        LoxTestUtil.beforeClass();
    }
    @AfterEach
    public void after() {
        LoxTestUtil.afterEach();
    }

}

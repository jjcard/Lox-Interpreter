package jlox.interpreters.lox;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

class BaseLoxTest {

    @BeforeAll
    public static void beforeClass() throws IOException {
        Lox.clearState();
        LoxTestUtil.beforeClass();
    }
    @AfterEach
    public void after() {
        LoxTestUtil.afterEach();
    }

}

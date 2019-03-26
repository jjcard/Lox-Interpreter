package jlox.interpreters.lox;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

class BaseLoxTest {

    @BeforeAll
    public static void beforeClass() throws IOException {
        LoxTestUtil.beforeClass();
    }
    @AfterEach
    public void after() {
        LoxTestUtil.afterEach();
    }
    @AfterAll
    public static void afterClass() throws IOException {
        LoxTestUtil.afterClass();
    }

}

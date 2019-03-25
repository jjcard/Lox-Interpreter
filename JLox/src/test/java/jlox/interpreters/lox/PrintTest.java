package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PrintTest {
    
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
    
    @Test
    void printString() throws IOException {
        Lox.run("print \"Hello World\";");
        
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("Hello World");
    }
    @Test
    void printTrue() throws IOException {
        Lox.run("print true;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("true");
    }
    @Test
    void printFalse() throws IOException {
        Lox.run("print false;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("false");
    }
    @Test
    void printInteger() throws IOException {
        Lox.run("print 1;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("1");

    }
    @Test
    void printFloat() throws IOException {
        Lox.run("print 1.1;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("1.1");

    }
}

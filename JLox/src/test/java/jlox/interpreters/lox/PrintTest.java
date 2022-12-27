package jlox.interpreters.lox;

import org.junit.jupiter.api.Test;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

class PrintTest extends BaseLoxTest {
    
    @Test
    void printString() {
        Lox.run("print \"Hello World\";");
        
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("Hello World");
    }
    @Test
    void printTrue() {
        Lox.run("print true;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("true");
    }
    @Test
    void printFalse() {
        Lox.run("print false;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("false");
    }
    @Test
    void printInteger() {
        Lox.run("print 1;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("1");

    }
    @Test
    void printFloat() {
        Lox.run("print 1.1;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("1.1");

    }
}

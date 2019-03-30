package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

import org.junit.jupiter.api.Test;

class MathTest extends BaseLoxTest {

    @Test
    void plus() {
        Lox.run("print 1 + 2;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("3");
    }
    @Test
    void minus() {
        Lox.run("print 10 - 3;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("7");
    }
    
    @Test
    void multiply() {
        Lox.run("print 10 * 3;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("30");
    }
    
    @Test
    void division() {
        Lox.run("print 100 / 50;");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("2");
    }

}

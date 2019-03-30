package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

import org.junit.jupiter.api.Test;

class LogicTest extends BaseLoxTest {

    @Test
    void trueAndtrue() {
        Lox.run("print true and true;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("true");
    }
    @Test
    void trueAndfalse() {
        Lox.run("print true and false;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("false");
    }
    @Test
    void falseAndfalse() {
        Lox.run("print false and false;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("false");
    }
    
    @Test
    void trueOrtrue() {
        Lox.run("print true or true;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("true");
    }
    @Test
    void trueOrfalse() {
        Lox.run("print true or false;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("true");
    }
    @Test
    void falseOrfalse() {
        Lox.run("print false or false;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("false");
    }
}

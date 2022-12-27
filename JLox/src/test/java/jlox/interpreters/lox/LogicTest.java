package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

import org.junit.jupiter.api.Test;

class LogicTest extends BaseLoxTest {

    @Test
    void trueAndTrue() {
        Lox.run("print true and true;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("true");
    }
    @Test
    void trueAndFalse() {
        Lox.run("print true and false;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("false");
    }
    @Test
    void falseAndFalse() {
        Lox.run("print false and false;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("false");
    }
    
    @Test
    void trueOrTrue() {
        Lox.run("print true or true;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("true");
    }
    @Test
    void trueOrFalse() {
        Lox.run("print true or false;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("true");
    }
    @Test
    void falseOrFalse() {
        Lox.run("print false or false;");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("false");
    }
}

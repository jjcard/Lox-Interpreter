package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

import org.junit.jupiter.api.Test;

class CommentTest extends BaseLoxTest {

    @Test
    public void lineCommentTest() {
        Lox.run("//print \"hello\";\r\n" + 
                "print \"World\";");
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("World");
    }

    @Test
    void blockCommentTest() {
        Lox.run("/**\r\n" + 
                "fdsablal\r\n" + 
                "//inside comment\r\n" + 
                "print \"hello\";\r\n" + 
                "*/\r\n" + 
                "print \"World\";");
        
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("World");
    }

}

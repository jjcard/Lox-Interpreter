package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class ForLoopTest extends BaseLoxTest {
    private static final String FOR_TEST_FILE_DIR = TEST_FILE_DIR + "for/";

    @Test
    void forLoopTest_full() {
        final String loxCode = ""
                + "for (var i = 0; i<=4; i = i+1){\r\n" + 
                "    print i;\r\n" + 
                "}";
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("0", "1", "2", "3", "4");
    }
    
    @Test
    void forLoopTest_continueBreak() {
        final String loxCode = "for (var i = 0; i<=10; i = i+1){\r\n" + 
                "    if (i == 4 or i == 6){\r\n" + 
                "        continue;\r\n" + 
                "    }\r\n" + 
                "    if (i == 8){\r\n" + 
                "        break;\r\n" + 
                "    }\r\n" + 
                "    print i;\r\n" + 
                "}";
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("0", "1", "2", "3", "5", "7");
    }
    
    //******From test suite
    @Test
    public void classInBody() throws IOException {
        Lox.runFile(FOR_TEST_FILE_DIR+"class_in_body.lox");
        LoxTestUtil.assertErrorLineEquals("[line 2] Error at 'class': Expect expression.");
    }
    @Test
    public void closureInBody() throws IOException {
       Lox.runFile(FOR_TEST_FILE_DIR+"closure_in_body.lox");
       LoxTestUtil.assertHasNoErrors();
       assertLinesEquals("1", "2", "3");
    }
    @Test
    public void funInBody() throws IOException {
        Lox.runFile(FOR_TEST_FILE_DIR + "fun_in_body.lox");
        LoxTestUtil.assertErrorLineEquals("[line 2] Error at 'fun': Expect expression.");
    }
    @Test
    public void scope() throws IOException {
       Lox.runFile(FOR_TEST_FILE_DIR+"scope.lox");
       LoxTestUtil.assertHasNoErrors();
       assertLinesEquals("0", "-1", "after","0");
    }

}

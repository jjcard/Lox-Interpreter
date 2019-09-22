package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;

import org.junit.jupiter.api.Test;

public class LoopTest extends BaseLoxTest {

    @Test
    public void whileloopTest() {
        final String loxCode = ""
                + "var i = 0;\r\n"
                + "while (i<=4){\r\n"
                + "    print i;\r\n" 
                + "    i = i+1;\r\n" 
                +"}";
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("0", "1", "2", "3", "4"); 
    }
    @Test
    public void whileloopTest_break() {
        final String loxCode = "var i = 0;\r\n" + 
                "while (i <=4){\r\n" + 
                "    if (i == 3){\r\n" + 
                "        break;\r\n" + 
                "    }\r\n" + 
                "    print i;\r\n" +
                "    i = i+1;\r\n" + 
                "}";
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("0", "1", "2"); 
    }

    
    @Test
    public void breakStatmentOutsideLoop() {
        final String loxCode = "var a = \"yes\";\r\n" + 
                "break;";
        Lox.run(loxCode);
        LoxTestUtil.assertErrorMessageEquals("Break statement must be inside a loop.", 2, " at 'break'");
    }
    @Test
    public void continueStatmentOutsideLoop() {
        final String loxCode = "var a = \"yes\";\r\n" + 
                "var b = a;\r\n" + 
                "continue;";
        Lox.run(loxCode);
        LoxTestUtil.assertErrorMessageEquals("Continue statement must be inside a loop.", 3, " at 'continue'");
    }
}

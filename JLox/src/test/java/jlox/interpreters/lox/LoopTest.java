package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;

import org.junit.jupiter.api.Test;

public class LoopTest extends BaseLoxTest {

    @Test
    public void whileloopTest() {
        final String loxCode = """
                var i = 0;\r
                while (i<=4){\r
                    print i;\r
                    i = i+1;\r
                }""";
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("0", "1", "2", "3", "4"); 
    }
    @Test
    public void whileloopTest_break() {
        final String loxCode = """
                var i = 0;\r
                while (i <=4){\r
                    if (i == 3){\r
                        break;\r
                    }\r
                    print i;\r
                    i = i+1;\r
                }""";
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("0", "1", "2"); 
    }

    
    @Test
    public void breakStatementOutsideLoop() {
        final String loxCode = "var a = \"yes\";\r\n" + 
                "break;";
        Lox.run(loxCode);
        LoxTestUtil.assertErrorMessageEquals("Break statement must be inside a loop.", 2, " at 'break'");
    }
    @Test
    public void continueStatementOutsideLoop() {
        final String loxCode = """
                var a = "yes";\r
                var b = a;\r
                continue;""";
        Lox.run(loxCode);
        LoxTestUtil.assertErrorMessageEquals("Continue statement must be inside a loop.", 3, " at 'continue'");
    }
}

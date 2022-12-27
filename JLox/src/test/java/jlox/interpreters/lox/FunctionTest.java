package jlox.interpreters.lox;

import org.junit.jupiter.api.Test;

class FunctionTest extends BaseLoxTest {

    @Test
    public void multiArgumentFunctionTest() {
        final String loxCode = """
                fun add(a, b, c) {\r
                  print a + b + c;\r
                }\r
                add(1,4,2);""";
        
        Lox.run(loxCode);
        LoxTestUtil.assertHasNoErrors();
        LoxTestUtil.assertLineEquals("7");
    }

    @Test
    public void functionCalledWithTooManyArguments() {
        final String loxCode = """
                fun oneArg(one){\r
                    print one;\r
                }\r
                var a = "one";\r
                var b = "two";\r
                oneArg(a,b);""";
        Lox.run(loxCode);
        LoxTestUtil.assertRuntimeError("Expected 1 arguments but got 2.", 6);
    }

}

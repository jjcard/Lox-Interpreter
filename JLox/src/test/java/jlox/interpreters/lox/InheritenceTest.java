package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;

import org.junit.jupiter.api.Test;

public class InheritenceTest extends BaseLoxTest {

    
    @Test
    public void superMethodTest() {
        final String loxCode = """
                class Doughnut {\r
                  cook() {\r
                    print "Fry until golden brown.";\r
                  }\r
                }\r
                \r
                class BostonCream < Doughnut {\r
                  cook() {\r
                    super.cook();\r
                    print "Pipe full of custard and coat with chocolate.";\r
                  }\r
                }\r
                \r
                BostonCream().cook();""";
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("Fry until golden brown.", "Pipe full of custard and coat with chocolate.");
    }
    
    @Test
    public void superMethodFromSuperclassTest() {
        final String loxCode = """
                class A {\r
                  method() {\r
                    print "A method";\r
                  }\r
                }\r
                \r
                class B < A {\r
                  method() {\r
                    print "B method";\r
                  }\r
                \r
                  test() {\r
                    super.method();\r
                  }\r
                }\r
                \r
                class C < B {}\r
                \r
                C().test();""";
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("A method");
    }
}

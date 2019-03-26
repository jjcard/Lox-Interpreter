package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;

import org.junit.jupiter.api.Test;

public class InheritenceTest extends BaseLoxTest {

    
    @Test
    public void superMethodTest() {
        final String loxCode = "class Doughnut {\r\n" + 
                "  cook() {\r\n" + 
                "    print \"Fry until golden brown.\";\r\n" + 
                "  }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "class BostonCream < Doughnut {\r\n" + 
                "  cook() {\r\n" + 
                "    super.cook();\r\n" + 
                "    print \"Pipe full of custard and coat with chocolate.\";\r\n" + 
                "  }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "BostonCream().cook();";
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("Fry until golden brown.", "Pipe full of custard and coat with chocolate.");
    }
    
    @Test
    public void superMethodFromSuperclassTest() {
        final String loxCode = "class A {\r\n" + 
                "  method() {\r\n" + 
                "    print \"A method\";\r\n" + 
                "  }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "class B < A {\r\n" + 
                "  method() {\r\n" + 
                "    print \"B method\";\r\n" + 
                "  }\r\n" + 
                "\r\n" + 
                "  test() {\r\n" + 
                "    super.method();\r\n" + 
                "  }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "class C < B {}\r\n" + 
                "\r\n" + 
                "C().test();";
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("A method");
    }
}

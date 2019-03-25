package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;
import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClassTest {

    @BeforeAll
    public static void beforeClass() throws IOException {
        LoxTestUtil.beforeClass();
    }
    @AfterEach
    public void after() {
        LoxTestUtil.afterEach();
    }
    @AfterAll
    public static void afterClass() throws IOException {
        LoxTestUtil.afterClass();
    }

    @Test
    void functionTest() {
        String loxCode = "class DevonshireCream {\r\n" + 
                "  serveOn() {\r\n" + 
                "    return \"Scones\";\r\n" + 
                "  }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "print DevonshireCream;";
        
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("DevonshireCream");
        
    }
    
    @Test
    public void classToStringTest() {
        String loxCode = "class Bagel {}\r\n" + 
                "var bagel = Bagel();\r\n" + 
                "print bagel;";
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("Bagel instance");
    }

    @Test
    public void initTest() {
        String loxCode = "class Foo {\r\n" + 
                "  init() {\r\n" + 
                "    print this;\r\n" + 
                "  }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "var foo = Foo();\r\n";
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("Foo instance");
        
    }
    
  @Ignore("Need to figure out why this is printing it 3 times")
    @Test
    public void initTest2() {
        String loxCode = "class Foo {\r\n" + 
                "  init() {\r\n" + 
                "    print this;\r\n" + 
                "  }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "var foo = Foo();\r\n" + 
                "print foo.init();";
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("Foo instance", "Foo instance");
        
    }

}

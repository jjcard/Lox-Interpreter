package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;
import static jlox.interpreters.lox.LoxTestUtil.assertLinesEquals;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ClassTest extends BaseLoxTest {
    private static final String CLASS_TEST_FILE_DIR = TEST_FILE_DIR + "class/";
    @Test
    void functionTest() {
        String loxCode = """
                class DevonshireCream {\r
                  serveOn() {\r
                    return "Scones";\r
                  }\r
                }\r
                \r
                print DevonshireCream;""";
        
        
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("DevonshireCream");
        
    }
    
    @Test
    public void classToStringTest() {
        String loxCode = """
                class Bagel {}\r
                var bagel = Bagel();\r
                print bagel;""";
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        
        assertLineEquals("Bagel instance");
    }

    @Test
    public void initTest() {
        String loxCode = """
                class Foo {\r
                  init() {\r
                    print this;\r
                  }\r
                }\r
                \r
                var foo = Foo();\r
                """;
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("Foo instance");
    }
    
    @Test
    public void initTest2() {
        String loxCode = """
                class Foo {\r
                  init() {\r
                    print this;\r
                  }\r
                }\r
                \r
                var foo = Foo();\r
                print foo.init();""";
        Lox.run(loxCode);
        
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("Foo instance", "Foo instance", "Foo instance");
    }
    
    @Disabled("test for static methods, if implemented")
    @Test
    public void staticMethodTest() {
        String loxCode = """
                class Math {\r
                  class square(n) {\r
                    return n * n;\r
                  }\r
                }\r
                \r
                print Math.square(3);.""";
        
        Lox.run(loxCode);
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("9");
        
    }
    
    //****From Test Suite (done)
    
    @Test
    void empty() throws IOException {
        Lox.runFile(CLASS_TEST_FILE_DIR + "empty.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("Foo");
    }

    @Test
    void inheritSelf() throws IOException {
        Lox.runFile(CLASS_TEST_FILE_DIR + "inherit_self.lox");
        LoxTestUtil.assertErrorLineEquals("[line 1] Error at 'Foo': A class cannot inherit from itself.");
    }

    @Test
    void inheritedMethod() throws IOException {
        Lox.runFile(CLASS_TEST_FILE_DIR + "inherited_method.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLinesEquals("in foo", "in bar", "in baz");
    }

    @Test
    void localInheritOther() throws IOException {
        Lox.runFile(CLASS_TEST_FILE_DIR + "local_inherit_other.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("B");
    }

    @Test
    void localInheritSelf() throws IOException {
        Lox.runFile(CLASS_TEST_FILE_DIR + "local_inherit_self.lox");
        LoxTestUtil.assertErrorLineEquals("[line 2] Error at 'Foo': A class cannot inherit from itself.");
    }

    @Test
    void localReferenceSelf() throws IOException {
        Lox.runFile(CLASS_TEST_FILE_DIR + "local_reference_self.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("Foo");
    }

    @Test
    void referenceSelf() throws IOException {
        Lox.runFile(CLASS_TEST_FILE_DIR + "reference_self.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("Foo");
    }
}

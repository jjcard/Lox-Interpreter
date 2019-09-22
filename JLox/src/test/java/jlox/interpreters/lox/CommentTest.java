package jlox.interpreters.lox;

import static jlox.interpreters.lox.LoxTestUtil.assertLineEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class CommentTest extends BaseLoxTest {

    private static final String COMMENT_TEST_FILE_DIR = TEST_FILE_DIR + "comments/";
    @Test
    void lineCommentTest() {
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
    //*******Tests from the test suite (done)
    @Test
    void lineAtEOF() throws IOException {
        Lox.runFile(COMMENT_TEST_FILE_DIR+"line_at_eof.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("ok");
    }
    @Test
    void onlyLineComment() throws IOException {
        Lox.runFile(COMMENT_TEST_FILE_DIR + "only_line_comment.lox");
        LoxTestUtil.assertHasNoErrors();
        LoxTestUtil.assertNoLines();
    }
    @Test
    void onlyLineCommentAddLine() throws IOException {
        Lox.runFile(COMMENT_TEST_FILE_DIR + "only_line_comment_and_line.lox");
        LoxTestUtil.assertHasNoErrors();
        LoxTestUtil.assertNoLines();
    }
    @Test
    void unicode() throws IOException {
        Lox.runFile(COMMENT_TEST_FILE_DIR+"unicode.lox");
        LoxTestUtil.assertHasNoErrors();
        assertLineEquals("ok");
    }

}

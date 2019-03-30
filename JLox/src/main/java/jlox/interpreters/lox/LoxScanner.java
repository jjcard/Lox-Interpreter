package jlox.interpreters.lox;

import static jlox.interpreters.lox.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
public class LoxScanner {
    
    private static final Map<String, TokenType> KEYWORDS;
    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("and",    AND);                       
        KEYWORDS.put("class",  CLASS);                     
        KEYWORDS.put("else",   ELSE);                      
        KEYWORDS.put("false",  FALSE);                     
        KEYWORDS.put("for",    FOR);                       
        KEYWORDS.put("fun",    FUN);                       
        KEYWORDS.put("if",     IF);                        
        KEYWORDS.put("nil",    NIL);                       
        KEYWORDS.put("or",     OR);                        
        KEYWORDS.put("print",  PRINT);                     
        KEYWORDS.put("return", RETURN);                    
        KEYWORDS.put("super",  SUPER);                     
        KEYWORDS.put("this",   THIS);                      
        KEYWORDS.put("true",   TRUE);                      
        KEYWORDS.put("var",    VAR);                       
        KEYWORDS.put("while",  WHILE);
        KEYWORDS.put("break",  BREAK); 
        KEYWORDS.put("continue", CONTINUE);
        KEYWORDS.put("import", IMPORT);
    }
    
    private final String source;                                            
    private final List<Token> tokens = new ArrayList<>();
    /** First character of current lexeme*/
    private int start = 0;
    /** Current character looking at*/
    private int current = 0;                             
    private int line = 1;   

    public LoxScanner(String source) {
      this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {                            
            // We are at the beginning of the next lexeme.
            start = current;                              
            scanToken();                     
          }

          tokens.add(new Token(EOF, "", null, line));     
          return tokens; 
    }

    private void scanToken() {                     
        char c = advance();          
        switch (c) {                                 
          case '(': addToken(LEFT_PAREN); break;     
          case ')': addToken(RIGHT_PAREN); break;    
          case '{': addToken(LEFT_BRACE); break;     
          case '}': addToken(RIGHT_BRACE); break;    
          case ',': addToken(COMMA); break;          
          case '.': addToken(DOT); break;            
          case '-': addToken(MINUS); break;          
          case '+':
                if (match('+')) {
                    addToken(PLUS_PLUS);
                } else if (match('=')) {
                    addToken(PLUS_EQUAL);
                } else {
                    addToken(PLUS);
                }
              break;           
          case ';': addToken(SEMICOLON); break;
          case '*': addToken(STAR); break;
          case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;      
          case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;    
          case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;      
          case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
          case '/':
              if (match('/')) {
                  //skip single line comment
                  while (peek() != '\n' && !isAtEnd()) advance();
              } else if (match('*')) {
                  blockComment();
              } else {
                  addToken(SLASH);
              }
              break;
          case ' ':
          case '\r':
          case '\t':
            // Ignore whitespace.                      
            break;

          case '\n':                                   
            line++;                                    
            break;
          case '"': string(); break;
          default: 
              if (isDigit(c)) {
                  number();
              } else if (isAlpha(c)) {
                  identifier();
              } else {
                 Lox.error(line, "Unexpected character."); 
              }
              
              break;
        }                                            
      }  
    /**
     * Read in non-nested block comment
     */
    private void blockComment() {
        
//        while (peek() != '*' && peekNext() != '/' && !isAtEnd()) {
          while ( !(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
            char c = advance();
            if (c == '\n') {
                line++;
            }
        }
        
        if (isAtEnd()) {                                        
            Lox.error(line, "Unterminated block comment.");              
            return;                                               
          }
        
        //injest the */
        advance();
        advance();
        
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        final String text = source.substring(start, current);
        final TokenType type = KEYWORDS.getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));

    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {                   
            if (peek() == '\n') line++;                           
            advance();                                            
          }

          // Unterminated string.                                 
          if (isAtEnd()) {                                        
            Lox.error(line, "Unterminated string.");              
            return;                                               
          }                                                       

          // The closing ".                                       
          advance();                                              

          // Trim the surrounding quotes.                         
          final String value = source.substring(start + 1, current - 1);
          addToken(STRING, value); 
    }

    /**
     * returns if next character is expected char. If is, advances current pointer.
     * @param expected
     * @return
     */
    private boolean match(final char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        advance();
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }
    private char peekNext() {                         
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);              
      } 
    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        final String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    
    private static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAlpha(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private static boolean isAlphaNumeric(final char c) {
        return isAlpha(c) || isDigit(c);
    }
}

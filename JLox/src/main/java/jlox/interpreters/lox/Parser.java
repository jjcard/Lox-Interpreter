package jlox.interpreters.lox;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import jlox.interpreters.lox.Expr.Get;

import static jlox.interpreters.lox.TokenType.*;

class Parser {
    private static final int MAX_ARGS_SIZE = 8;

    /** Sentinel Exception*/
    @SuppressWarnings("serial")
    private static class ParseError extends RuntimeException{}
    private final List<Token> tokens;
    private int current = 0;
    private int loopLevel = 0;
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    List<Stmt> parse() {
        final List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }
    /**
     * declaration → classDecl
                    | funDecl
                    | varDecl
                    | statement ;
     * @return Stmt
     */
    private Stmt declaration() {                
        try {
            if (match(CLASS)) {
                return classDeclaration();
            }
            if (match(FUN)) {
                return function("function");
            }
            if (match(VAR)) {
                return varDeclarationFinish();
            }

          return statement();                     
        } catch (ParseError error) {              
          synchronize();                          
          return null;                            
        }                                         
      }

    /**
     * classDecl → "class" IDENTIFIER ( "<" IDENTIFIER )?
            "{" function* "}" ;
     */
    private Stmt classDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");
        
        Expr.Variable superclass = null;
        if (match(LESS)) {
            consume(TokenType.IDENTIFIER, "Expect superclass name.");
            superclass = Expr.Variable.of(previous());
        }
        
        consume(LEFT_BRACE, "Expect '{' before class body.");
        
        List<Stmt.Function> methods = new ArrayList<>();
        while(!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"));
        }
        consume(RIGHT_BRACE, "Expect '}' after class body.");
        
        return Stmt.Class.of(name, superclass, methods);
    }
    /**
     * function → IDENTIFIER "(" parameters? ")" block ;
     * @param kind
     * @return
     */
    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= MAX_ARGS_SIZE) {
                    error(peek(), "Cannot have more then 8 parameters.");
                } 
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        
        //body
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = blockFinish();
        
        return Stmt.Function.of(name, parameters, body);
    }
    /**
        statement → exprStmt
                  | forStmt
                  | ifStmt
                  | printStmt
                  | whileStmt
                  | breakStmt
                  | continueStmt
                  | block ;

     */
    private Stmt statement() {
        if (match(FOR)) {
            return forStatementFinish();
        } else if (match(IF)) {
          return ifStatementFinish();  
        } else if (match(PRINT)) {
            return printStatementFinish();
        } else if (match(RETURN)) {
            return returnStatementFinish();
        } else if (match(WHILE)) {
            return whileStatementFinish();
        } else if (match(BREAK)) {
            return breakStatementFinish();
        } else if (match(CONTINUE)) {
            return continueStatementFinish();
        } else if (match(LEFT_BRACE)){
            return Stmt.Block.of(blockFinish());
        } else {
            return expressionStatement();
        }
    }
    private Stmt breakStatementFinish() {
        if (loopLevel <= 0) {
            throw error(previous(), "Break statement must be inside a loop.");
        }
        consume(SEMICOLON, "Expect ';' after 'break' statement.");
        return Stmt.Break.of();
    }
    private Stmt continueStatementFinish() {
        if (loopLevel <= 0) {
            throw error(previous(), "Continue statement must be inside a loop.");
        }
        consume(SEMICOLON, "Expect ';' after 'continue' statement.");
        return Stmt.Continue.of();
    }
    private Stmt returnStatementFinish() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }
    private Stmt forStatementFinish() {
        //since we have a 'continue' statement, this can no longer be just 'syntactic sugar'
        //except for the initializer
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        final Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclarationFinish();
        } else {
            initializer = expressionStatement();
        }

        final Expr condition;
        if (!check(SEMICOLON)) {
            condition = expression();
        } else {
            condition = Expr.Literal.of(true);
        }
        
        consume(SEMICOLON, "Expect ';' after loop condition.");
        
        Expr increment = null;                                 
        if (!check(RIGHT_PAREN)) {                             
          increment = expression();                            
        }                                                      
        consume(RIGHT_PAREN, "Expect ')' after for clauses."); 
        
        try {
            loopLevelStart();
            Stmt body = statement();
            Stmt incrementStmt;
            if (increment != null) {
                incrementStmt =  Stmt.Expression.of(increment);
            } else {
                incrementStmt = null;
            }

            body = Stmt.For.of(condition, incrementStmt, body);
            if (initializer != null) {
                body = Stmt.Block.of(Arrays.asList(initializer, body));
            }
            
            return body;
        } finally {
            loopLevelEnd();
        }
    }
    private Stmt whileStatementFinish() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        final Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        
        try {
            loopLevelStart();
            final Stmt body = statement();
            
            return Stmt.While.of(condition, body);            
        } finally {
            loopLevelEnd();
        }

    }
    private void loopLevelStart() {
        loopLevel++;
    }
    private void loopLevelEnd() {
        loopLevel--;
    }
    private Stmt ifStatementFinish() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");
        Stmt thenBranch = statement();
       final Stmt elseBranch;
        if (match(ELSE)) {
            elseBranch = statement();
        } else {
            elseBranch = null;
        }
        return Stmt.If.of(condition, thenBranch, elseBranch);
    }
    /**
     * block     → "{" declaration* "}" ;
     * @return
     */
    private List<Stmt> blockFinish() {
        final List<Stmt> statements = new ArrayList<>();
        while(!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }
    /**
     * exprStmt  → expression ";" ;
     * @return
     */
    private Stmt expressionStatement() {
        final Expr value = expression();
        consume(SEMICOLON, "Expected ';', after expression");
        return Stmt.Expression.of(value);
    }
    /**
     * printStmt → "print" expression ";" ;
     */
    private Stmt printStatementFinish() {
        final Expr value = expression();
        consume(SEMICOLON, "Expected ';', after value");
        return Stmt.Print.of(value);
    }
    private Stmt varDeclarationFinish() {
        final Token name = consume(IDENTIFIER, "Expected variable name.");
        final Expr initializer;
        if (match(EQUAL)) {
            initializer = expression();
        } else {
            initializer = null;
        }
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return Stmt.Var.of(name, initializer);
    }
    
    private Expr expression() {
        return assignment();
    }
    /**
     * assignment → IDENTIFIER "=" assignment
           | logic_or ;
     */
    private Expr assignment() {
        Expr expr = or();
        
        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return Expr.Assign.of(name, value);
            } else if (expr instanceof Expr.Get) {
                //assignment with Get syntax means it's really a Expr.Set
                Expr.Get get = (Get) expr;
                return Expr.Set.of(get.object, get.name, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }
    /**
     * logic_or → logic_and ( "or" logic_and )* ;
     */
    private Expr or() {
       Expr expr = and();
       while(match(OR)) {
           Token operator = previous();
           Expr right = and();
           expr = new Expr.Logical(expr, operator, right);
       }
       return expr;
    }
    private Expr and() {
        Expr expr = equality();
        while(match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }
    /**
     * equality → comparison ( ( "!=" | "==" ) comparison )* ;
     */
    private Expr equality() {
        return buildAssociativeeftBinaryExpression(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }
    /**
     * comparison → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
     */
    private Expr comparison() {
        return buildAssociativeeftBinaryExpression(this::addition, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }
    
    /**
     * addition → multiplication ( ( "-" | "+" ) multiplication )* ;
     */
    private Expr addition() {
       return buildAssociativeeftBinaryExpression(this::multiplication, MINUS, PLUS);
    }
    /**
     * multiplication → unary ( ( "/" | "*" ) unary )* ;
     * @return
     */
    private Expr multiplication() {
        return buildAssociativeeftBinaryExpression(this::unary, SLASH, STAR);
    }
    private Expr buildAssociativeeftBinaryExpression(Supplier<Expr> nextPrecedent, TokenType...types) {
        Expr expr = nextPrecedent.get();
        
        while (match(types)) {
            Token operator = previous();
            Expr right = nextPrecedent.get();
            expr = Expr.Binary.of(expr, operator, right);
            
        }
        return expr;
    }
    /**
     * unary → ( "!" | "-" ) unary | call ;
     * @return
     */
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return Expr.Unary.of(operator, right);
        }
        
//        return primary();
        return call();
    }
    /**
     * call → primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
     * arguments → expression ( "," expression )* ;
     * @return
     */
    private Expr call() {
        Expr expr = primary();
        
        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DOT)) {
                Token name = consume (IDENTIFIER, "Expect property name after '.'.");
                expr = Expr.Get.of(expr, name); 
            } else {
                break;
            }
        }
        return expr;
    }
    /**
     * arguments → expression ( "," expression )* ;
     * @param expr
     * @return
     */
    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= MAX_ARGS_SIZE) {
                    error(peek(), "Cannot have more than 8 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }
    /**
     * primary → "true" | "false" | "nil" | "this"
        | NUMBER | STRING | IDENTIFIER | "(" expression ")"
        | "super" "." IDENTIFIER ;
     * @return
     */
    private Expr primary() {
        if (match(FALSE)) {
            return Expr.Literal.of(false);
        }
        if (match(TRUE)) {
            return Expr.Literal.of(true);
        }
        if (match(NIL)) {
            return Expr.Literal.of(null);
        }
        if (match(NUMBER, STRING)) {
            return Expr.Literal.of(previous().literal);
        }
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return Expr.Grouping.of(expr);
        }
        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER, "Expect superclass method name.");
            return Expr.Super.of(keyword, method);
        }
        if (match(THIS)) {
            return Expr.This.of(previous());
        }
        if (match(IDENTIFIER)) {
            return Expr.Variable.of(previous());
        }
        throw error(peek(), "Expect expression.");  
    }


    private Token consume(TokenType type, String message) throws ParseError {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
        
    }


    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    @SuppressWarnings("incomplete-switch")
    private void synchronize() {                 
        advance();

        while (!isAtEnd()) {                       
          if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }                                     

          advance();                               
        }                                          
      }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}

package jlox.interpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jlox.interpreters.lox.Expr.Assign;
import jlox.interpreters.lox.Expr.Binary;
import jlox.interpreters.lox.Expr.Call;
import jlox.interpreters.lox.Expr.Get;
import jlox.interpreters.lox.Expr.Grouping;
import jlox.interpreters.lox.Expr.Literal;
import jlox.interpreters.lox.Expr.Logical;
import jlox.interpreters.lox.Expr.Set;
import jlox.interpreters.lox.Expr.Super;
import jlox.interpreters.lox.Expr.This;
import jlox.interpreters.lox.Expr.Unary;
import jlox.interpreters.lox.Expr.Variable;
import jlox.interpreters.lox.Stmt.Block;
import jlox.interpreters.lox.Stmt.Break;
import jlox.interpreters.lox.Stmt.Class;
import jlox.interpreters.lox.Stmt.Continue;
import jlox.interpreters.lox.Stmt.Expression;
import jlox.interpreters.lox.Stmt.For;
import jlox.interpreters.lox.Stmt.Function;
import jlox.interpreters.lox.Stmt.If;
import jlox.interpreters.lox.Stmt.Print;
import jlox.interpreters.lox.Stmt.Return;
import jlox.interpreters.lox.Stmt.Var;
import jlox.interpreters.lox.Stmt.While;

/**
 * Variable Resolution Pass.
 * No side effects, doesn't actually perform anything.
 * No Control Flow: visits all branches.
 *
 */
public class Resolver implements Expr.Visitor<Void>,  Stmt.Visitor<Void> {
    private enum FunctionType{
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }
    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }
    private final Interpreter interpreter;
    /** Stack of Maps, where Key is variable name, value is if it was initialized.*/
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    
    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;

    Resolver(Interpreter interpreter) {                             
      this.interpreter = interpreter;                               
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }


    private void beginScope() {
        scopes.push(new HashMap<>());
    }
    private void endScope() {
       scopes.pop();
        
    }
    void resolve(List<Stmt> statements) {
       for (Stmt statement: statements) {
           resolve(statement);
       }
        
    }

    private void resolve(Stmt statement) {
      statement.accept(this);
    }
    private void resolve(Expr expr) {
        expr.accept(this);
      }  
    @Override
    public Void visitExpressionStmt(Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function function) {
        declare(function.name);
        define(function.name);

        resolveFunction(function, FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        if(currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Cannot return from top-level code");
        }
        
        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.error(stmt.keyword, "Cannot return a value from an initializer.");
            }
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    private void define(Token name) {
       if (scopes.isEmpty()) {
           return;
       }
       scopes.peek().put(name.lexeme, true);
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Variable with this name already declared in this scope.");
        }
        scope.put(name.lexeme, false);
        
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) {
        //had to roll own, so might need to double-check this works
        resolve(stmt.condition);
        if (stmt.increment != null) {
            resolve(stmt.increment);
        }
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitBreakStmt(Break stmt) {
        //TODO maybe move break/continue in loop check to Resolver?
        return null;
    }

    @Override
    public Void visitContinueStmt(Continue stmt) {
        return null;
    }

    @Override
    public Void visitAssignExpr(Assign expr) {
        resolve(expr.value);                         
        resolveLocal(expr, expr.name); 
        return null;
    }

    @Override
    public Void visitBinaryExpr(Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Call expr) {
        resolve(expr.callee);
        for (Expr argument: expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGroupingExpr(Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name, "Cannot read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }

        // Not found. Assume it is global.
    }

    @Override
    public Void visitClassStmt(Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        declare(stmt.name);
        define(stmt.name);
        
        if (stmt.superclass != null) {
            if (stmt.name.lexeme.equals(stmt.superclass.name.lexeme)) {
                Lox.error(stmt.superclass.name, "A class cannot inherit from itself.");
            }
            
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass);
            
            beginScope();
            scopes.peek().put("super", true);
        }
        
        
        beginScope();
        scopes.peek().put("this", true);
        
        for (Stmt.Function method: stmt.methods) {
            FunctionType declaration = FunctionType.METHOD;
            if (LoxClass.isInitializer(method)) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method, declaration);
        }
        endScope();
        
        if (stmt.superclass != null) {
            endScope();
        }
        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitGetExpr(Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitSetExpr(Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }
    @Override
    public Void visitSuperExpr(Super expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.keyword, "Cannot use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            Lox.error(expr.keyword, "Cannot use 'super' in a class with no superclass.");
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }
    @Override
    public Void visitThisExpr(This expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.keyword, "Cannot use 'this' outside of a class.");
            return null;
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }

}

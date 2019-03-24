package jlox.interpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jlox.interpreters.lox.Expr.Assign;
import jlox.interpreters.lox.Expr.Binary;
import jlox.interpreters.lox.Expr.Get;
import jlox.interpreters.lox.Expr.Grouping;
import jlox.interpreters.lox.Expr.Literal;
import jlox.interpreters.lox.Expr.Logical;
import jlox.interpreters.lox.Expr.Set;
import jlox.interpreters.lox.Expr.Super;
import jlox.interpreters.lox.Expr.Unary;
import jlox.interpreters.lox.Expr.Variable;
import jlox.interpreters.lox.Propogator.BreakPropogator;
import jlox.interpreters.lox.Propogator.ContinuePropogator;
import jlox.interpreters.lox.Stmt.Block;
import jlox.interpreters.lox.Stmt.Break;
import jlox.interpreters.lox.Stmt.Class;
import jlox.interpreters.lox.Stmt.Continue;
import jlox.interpreters.lox.Stmt.Expression;
import jlox.interpreters.lox.Stmt.For;
import jlox.interpreters.lox.Stmt.Function;
import jlox.interpreters.lox.Stmt.If;
import jlox.interpreters.lox.Stmt.Print;
import jlox.interpreters.lox.Stmt.Var;
import jlox.interpreters.lox.Stmt.While;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{

    final Environment globals = new Environment();
    private Environment environment = globals;
    /**
     * at runtime, number of environments between current one and the enclosing one
     * where the interpreter can find the variable's value
     */
    private final Map<Expr, Integer> locals = new HashMap<>();
    
    
    
    public Interpreter() {
        globals.define("clock", new LoxCallable() {
            
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis()/1000.0;
            }
            
            @Override
            public int arity() {
                return 0;
            }
            @Override                                            
            public String toString() { return "<native fn>"; } 
        });
    }
    void interpret(List<Stmt> statements) {
        try {
           for (Stmt statement: statements) {
               execute(statement);
           }
        } catch (RuntimeError e) {
            Lox.runtimeError(e);
        }
        
    }
    @SuppressWarnings("incomplete-switch")
    @Override
    public Object visitBinaryExpr(Binary expr) {
        final Object left = evaluate(expr.left);
        final Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if (((double)right) == 0.0) {
                    throw new RuntimeError(expr.operator, "Cannot divide by zero");
                }
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or at least one string.");
        }
        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public Object visitUnaryExpr(Unary expr) {
        final Object right = evaluate(expr.right);
        switch(expr.operator.type) {
        case BANG:
            return !isTruthy(right);
        case MINUS:
            checkNumberOperand(expr.operator, right);
            return -(double) right;
        }
        
        return null;
    }
    @Override
    public Object visitVariableExpr(Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            //not found, must be global
            return globals.get(name);
        }
    }
    @Override
    public Object visitAssignExpr(Assign expr) {
        final Object value = evaluate(expr.value);
//        environment.assign(expr.name, value);
        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }
    
    @Override
    public Object visitLogicalExpr(Logical expr) {
        final Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            } 
        } else {
            //AND
            if (!isTruthy(left)) {
                return left;
            }
        }
        
        return evaluate(expr.right);
    }
    
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        
        final List<Object> arguments = new ArrayList<>();
        for (Expr argument: expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }
        LoxCallable function = (LoxCallable) callee;
        
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren,
                    "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
        
    }
    
    /**
     * 1. Evaluate the object.<br>
     * 2. Raise a runtime error if it’s not an instance of a class.<br>
     * 3. Evaluate the value.<br>
     * 
     * @throws RuntimeError
     *             if object not a LoxInstance
     */
    @Override
    public Object visitGetExpr(Get expr) throws RuntimeError {
        Object object = evaluate(expr.object);
        if (object instanceof LoxInstance) {
            return ((LoxInstance) object).get(expr.name);
        }
        throw new RuntimeError(expr.name, "Only instance have properties.");
    }
    
    @Override
    public Object visitSetExpr(Set expr) throws RuntimeError {
        Object object = evaluate(expr.object);
        
        if (! (object instanceof LoxInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }
        Object value = evaluate(expr.value);
        
        ((LoxInstance) object).set(expr.name, value);
        
        return value;
    }
    
    @Override
    public Object visitSuperExpr(Super expr) {
        int distance = locals.get(expr);
        LoxClass superclass = (LoxClass) environment.getAt(distance, "super");
        
        //this always refers to this instance, even if we are in a superclass
        //"this" is always one level nearer than "super"'s environment
        LoxInstance object = (LoxInstance) environment.getAt(distance -1, "this");
        
        LoxFunction method = superclass.findMethod(expr.method.lexeme);
        if (method == null) {
            throw new RuntimeError(expr.method, "Undefined property '" + expr.method.lexeme + "'.");
        }
        return method.bind(object);
    }
    
    @Override                                    
    public Object visitThisExpr(Expr.This expr) {
      return lookUpVariable(expr.keyword, expr); 
    }
    private void checkNumberOperand(Token operator, Object operand) {
        if (!(operand instanceof Double)) {
            throw new RuntimeError(operator, "Operand must be a number.");
        }
    }

    private static void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private static boolean isEqual(Object a, Object b) {
        return Objects.equals(a, b);
    }
    private static boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        return true;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private static String stringify(Object object) {
        if (object == null) {
            return "nil";
        }
        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }
    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }
    private Object execute(Stmt statement) {
        return statement.accept(this);
    }
    @Override
    public Void visitVarStmt(Var stmt) {
        final Object value;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        } else {
            //no initializer, set to 'nil'
            value = null;
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }
    
    @Override
    public Void visitClassStmt(Class stmt) throws RuntimeError {
        Object superclass = null;
        
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof LoxClass)) {
                throw new RuntimeError(stmt.superclass.name, "superclass must be a class.");
            }
        }
        
        environment.define(stmt.name.lexeme, null);
        
        if (stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }
        
        Map<String, LoxFunction> methods = new HashMap<>();
        for (Stmt.Function method: stmt.methods) {
            LoxFunction function = new LoxFunction(method, environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }
        
        LoxClass klass = new LoxClass(stmt.name.lexeme, (LoxClass) superclass, methods);
        
        if(superclass != null) {
            environment = environment.enclosing;
        }
        environment.assign(stmt.name, klass);
        
        
        return null;
    } 
    
    protected void executeBlock(List<Stmt> statements, Environment environment) {
       final Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement: statements) {
                execute(statement);
            }

        } finally {
            this.environment = previous;
        }

    }
    @Override
    public Void visitIfStmt(If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        
        return null;
    }
    @Override
    public Void visitWhileStmt(While stmt) {
        while(isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (ContinuePropogator e) {
                //continue
            } catch (BreakPropogator e) {
                break;
            }
        }
        return null;
    }
    
    @Override
    public Void visitForStmt(For stmt) {        
        while(isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (ContinuePropogator e) {
                //continue
            } catch (BreakPropogator e) {
                break;
            }
            if (stmt.increment != null) {
                execute(stmt.increment);
            }
            
        }
        return null;
    }
    
    @Override
    public Void visitFunctionStmt(Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment);
        environment.define(stmt.name.lexeme, function);
        return null;
    }
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) {
            value = evaluate(stmt.value);
        }
        throw new Propogator.ReturnPropogator(value);
    }
    @Override
    public Void visitBreakStmt(Break stmt) {
        throw new Propogator.BreakPropogator();
    }
    @Override
    public Void visitContinueStmt(Continue stmt) {
        throw new Propogator.ContinuePropogator();
    }
    
    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);          
      }



}

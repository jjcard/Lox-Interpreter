package jlox.interpreters.lox;

import jlox.interpreters.lox.Expr.Assign;
import jlox.interpreters.lox.Expr.Call;
import jlox.interpreters.lox.Expr.Logical;
import jlox.interpreters.lox.Expr.Variable;

public class AstPrinter implements Expr.Visitor<String> {
    
    String print(Expr expr) {
        return expr.accept(this);
    }
    
    @Override                                                          
    public String visitBinaryExpr(Expr.Binary expr) {                  
      return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override                                                          
    public String visitGroupingExpr(Expr.Grouping expr) {              
      return parenthesize("group", expr.expression);                   
    }                                                                  

    @Override                                                          
    public String visitLiteralExpr(Expr.Literal expr) {                
      if (expr.value == null) return "nil";                            
      return expr.value.toString();                                    
    }                                                                  

    @Override                                                          
    public String visitUnaryExpr(Expr.Unary expr) {                    
      return parenthesize(expr.operator.lexeme, expr.right);           
    }  
    
    private String parenthesize(String name, Expr...exprs){
        final StringBuilder builder = new StringBuilder();
        
        builder.append('(').append(name);
        for (Expr expr: exprs) {
            builder.append(' ')
            .append(expr.accept(this));
        }
        builder.append(')');
        
        return builder.toString();
    }

    @Override
    public String visitVariableExpr(Variable expr) {
        return "( var " + expr.name + ")";
    }

    @Override
    public String visitAssignExpr(Assign expr) {
        return "( assign " + expr.name + ")";
    }

    @Override
    public String visitLogicalExpr(Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Call expr) {
        // TODO Auto-generated method stub
        return null;
    }

}

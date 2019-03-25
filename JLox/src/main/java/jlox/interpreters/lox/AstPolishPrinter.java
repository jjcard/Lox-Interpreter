package jlox.interpreters.lox;

import jlox.interpreters.lox.Expr.Assign;
import jlox.interpreters.lox.Expr.Call;
import jlox.interpreters.lox.Expr.Get;
import jlox.interpreters.lox.Expr.Logical;
import jlox.interpreters.lox.Expr.Set;
import jlox.interpreters.lox.Expr.Super;
import jlox.interpreters.lox.Expr.This;
import jlox.interpreters.lox.Expr.Variable;

public class AstPolishPrinter implements Expr.Visitor<String> {
    
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return polishize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null)
            return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return polishize(expr.operator.lexeme, expr.right);
    }

    private String polishize(String name, Expr... exprs) {
        final StringBuilder builder = new StringBuilder();

        for (Expr expr : exprs) {
            builder.append(' ');
            builder.append(expr.accept(this));
        }
        builder.append(' ').append(name);
        return builder.toString();
    }

    @Override
    public String visitVariableExpr(Variable expr) {
        // /shrug
        return "( var " + expr.name + ")";
    }

    @Override
    public String visitAssignExpr(Assign expr) {
     // /shrug
        return "( assign " + expr.name + ")";
    }

    @Override
    public String visitLogicalExpr(Logical expr) {
        return polishize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Call expr) {
        //TODO double-check this
       return "( call " + expr.callee.accept(this) + ")";
    }

    @Override
    public String visitGetExpr(Get expr) {
        return "( get" + expr.name + ")";
    }

    @Override
    public String visitSetExpr(Set expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String visitThisExpr(This expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String visitSuperExpr(Super expr) {
        // TODO Auto-generated method stub
        return null;
    }

}

package jlox.interpreters.lox;

import jlox.interpreters.lox.Expr.Assign;
import jlox.interpreters.lox.Expr.Call;
import jlox.interpreters.lox.Expr.Logical;
import jlox.interpreters.lox.Expr.Variable;

public class AstPolishPrinter implements Expr.Visitor<String> {

    public static void main(String[] args) {

        Token plus = new Token(TokenType.PLUS, "+", null, 1);
        Token minus = new Token(TokenType.MINUS, "-", null, 1);
        Token multiply = new Token(TokenType.STAR, "*", null, 1);
        Expr expression = new Expr.Binary(
                new Expr.Grouping(Expr.Binary.of(new Expr.Literal(1), plus, new Expr.Literal(2))), multiply,
                new Expr.Grouping(new Expr.Binary(new Expr.Literal(4), minus, new Expr.Literal(3))));

        System.out.println(new AstPolishPrinter().print(expression));
    }  
    
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
        // TODO Auto-generated method stub
        return null;
    }

}

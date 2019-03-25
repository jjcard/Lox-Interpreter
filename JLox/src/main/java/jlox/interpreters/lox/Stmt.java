package jlox.interpreters.lox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitClassStmt(Class stmt);
    R visitExpressionStmt(Expression stmt);
    R visitFunctionStmt(Function stmt);
    R visitIfStmt(If stmt);
    R visitPrintStmt(Print stmt);
    R visitReturnStmt(Return stmt);
    R visitVarStmt(Var stmt);
    R visitWhileStmt(While stmt);
    R visitForStmt(For stmt);
    R visitBreakStmt(Break stmt);
    R visitContinueStmt(Continue stmt);
  }
  static class Block extends Stmt {
    static Block of(List<Stmt> statements) {
      return new Block(statements);
    }
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
  }
  static class Class extends Stmt {
    static Class of(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
      return new Class(name,superclass,methods);
    }
    Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
      this.name = name;
      this.superclass = superclass;
      this.methods = methods;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitClassStmt(this);
    }

    final Token name;
    final Expr.Variable superclass;
    final List<Stmt.Function> methods;
  }
  static class Expression extends Stmt {
    static Expression of(Expr expression) {
      return new Expression(expression);
    }
    Expression(Expr expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }
  static class Function extends Stmt {
    static Function of(Token name, List<Token> params, List<Stmt> body) {
      return new Function(name,params,body);
    }
    Function(Token name, List<Token> params, List<Stmt> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }

    final Token name;
    final List<Token> params;
    final List<Stmt> body;
  }
  static class If extends Stmt {
    static If of(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      return new If(condition,thenBranch,elseBranch);
    }
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
  static class Print extends Stmt {
    static Print of(Expr expression) {
      return new Print(expression);
    }
    Print(Expr expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }
  static class Return extends Stmt {
    static Return of(Token keyword, Expr value) {
      return new Return(keyword,value);
    }
    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    final Token keyword;
    final Expr value;
  }
  static class Var extends Stmt {
    static Var of(Token name, Expr initializer) {
      return new Var(name,initializer);
    }
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
  }
  static class While extends Stmt {
    static While of(Expr condition, Stmt body) {
      return new While(condition,body);
    }
    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }
  static class For extends Stmt {
    static For of(Expr condition, Stmt increment, Stmt body) {
      return new For(condition,increment,body);
    }
    For(Expr condition, Stmt increment, Stmt body) {
      this.condition = condition;
      this.increment = increment;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitForStmt(this);
    }

    final Expr condition;
    final Stmt increment;
    final Stmt body;
  }
  static class Break extends Stmt {
    static Break of() {
      return new Break();
    }
    Break() {
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBreakStmt(this);
    }

  }
  static class Continue extends Stmt {
    static Continue of() {
      return new Continue();
    }
    Continue() {
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitContinueStmt(this);
    }

  }

  abstract <R> R accept(Visitor<R> visitor);
}

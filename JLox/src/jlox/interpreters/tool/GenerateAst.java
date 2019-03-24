package jlox.interpreters.tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GenerateAst {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {                                        
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(1);                                              
          }                                                              
          final String outputDir = args[0];
          defineAst(outputDir, "Expr",
                  "Assign   : Token name, Expr value",
                  "Binary   : Expr left, Token operator, Expr right",
                  "Call     : Expr callee, Token paren, List<Expr> arguments",
                  "Get      : Expr object, Token name",
                  "Grouping : Expr expression",
                  "Literal  : Object value",
                  "Logical  : Expr left, Token operator, Expr right",
                  "Set      : Expr object, Token name, Expr value",
                  "This     : Token keyword",
                  "Unary    : Token operator, Expr right",
                  "Variable : Token name"
                );

          defineAst(outputDir, "Stmt",
                  "Block      : List<Stmt> statements",
                  "Class      : Token name, List<Stmt.Function> methods",
                  "Expression : Expr expression",
                  "Function   : Token name, List<Token> params, List<Stmt> body",
                  "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                  "Print      : Expr expression",
                  "Return     : Token keyword, Expr value",
                  "Var        : Token name, Expr initializer",
                  "While      : Expr condition, Stmt body",
                  "For        : Expr condition, Stmt increment, Stmt body",
                  "Break      : ",
                  "Continue   : "
                ); 

    }
    private static void defineAst(final String outputDir, final String baseName, String...types) throws IOException {
        
        PrintWriter writer = new PrintWriter(new File(outputDir, baseName + ".java"), StandardCharsets.UTF_8.name());
        
        writer.println("package jlox.interpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");               
        writer.println();                                    
        writer.println("abstract class " + baseName + " {");
        
        defineVistor(writer, baseName, types);

        for(String type: types) {
            String className = type.split(":")[0].trim();         
            String fields = type.split(":")[1].trim(); 
            defineType(writer, baseName, className, fields); 
        }
        
        // The base accept() method.                                   
        writer.println();
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");
        
        writer.println("}");                                    
        writer.close();     
        
    }

    private static void defineVistor(PrintWriter writer, String baseName, String[] types) {
        writer.println("  interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("  }");
    }
    private static void defineType(PrintWriter writer, final String baseName, final String className,
            final String fieldList) {
        
        final String[] fields;
        
        if (fieldList.trim().isEmpty()) {
            fields = new String[0];
        } else {
          fields = fieldList.split(", ");  
        }
        
        
        
        writer.println("  static class " + className + " extends " + baseName + " {");
        
        writer.println("    static " + className + " of(" + fieldList + ") {");
        writer.println("      return new " + className + "(" + Arrays.stream(fields).map(s->s.split(" ")[1]).collect(Collectors.joining(","))+ ");");
        writer.println("    }");
        // Constructor.
        writer.println("    " + className + "(" + fieldList + ") {");

        if (fields.length > 0) {
            // Store parameters in fields.
            for (String field : fields) {
                String name = field.split(" ")[1];
                writer.println("      this." + name + " = " + name + ";");
            }
        }


        writer.println("    }");

        // Visitor pattern.                                      
        writer.println();
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" + className + baseName + "(this);");
        writer.println("    }");
        
        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("  }");

    }

}

package util;

import java.util.List;

import lombok.*;

import com.google.gson.*;

public abstract class Stmt {

    public interface Visitor<R> {
	R visitBlockStmt(Block stmt);
	R visitClassStmt(Class stmt);
	R visitExpressionStmt(Expression stmt);
	R visitFunctionStmt(FunctionDecl stmt);
	R visitIfStmt(If stmt);
	R visitPrintStmt(Print stmt);
	R visitReturnStmt(Return stmt);
	R visitVarDefStmt(VarDef stmt);
	R visitAssignmentStmt(Assignment stmt);
	R visitWhileStmt(While stmt);
	R visitBreakStmt(Break stmt);
	R visitImportStmt(Import stmt);
    }

    @Getter @Setter @AllArgsConstructor
    public static class Block extends Stmt {

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitBlockStmt(this);
	}
	private final List<Stmt> statements;
    }

    @Getter @Setter @AllArgsConstructor
    public static class Class extends Stmt {
	
	@Getter @Setter @AllArgsConstructor
	public static class Attribute {
	    private final Token fieldName;
	    private final Token typeName;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitClassStmt(this);
	}

	private final Token name;
	private final List<Attribute> attributes;
	private final List<FunctionDecl> methods;
    }

    @Getter @Setter @AllArgsConstructor
    public static class Expression extends Stmt {

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitExpressionStmt(this);
	}

	final Expr expression;
    }

    @Getter @Setter @AllArgsConstructor
    public static class FunctionDecl extends Stmt {

	@Getter @Setter @AllArgsConstructor
	public static class Parameter {
	    private final Token varName;
	    private final Token typeName;
	}
	
	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitFunctionStmt(this);
	}

	private final Token name;
	private final Token returnType;
	private final List<FunctionDecl.Parameter> params;
	private final Stmt.Block body;
    }

    @Getter @Setter @AllArgsConstructor
    public static class If extends Stmt {

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitIfStmt(this);
	}

	final Expr condition;
	final Stmt.Block body;
	final Stmt.If thenBranch;
	final Stmt.If elseBranch;
    }

    @Getter @Setter @AllArgsConstructor
    public static class Print extends Stmt {

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitPrintStmt(this);
	}

	private final Token formatter;
	private final List<Expr> expressions;
    }



    @Getter @Setter @AllArgsConstructor
    public static class Return extends Stmt {

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitReturnStmt(this);
	}

	private final Token keyword;
	public final Expr value;
    }

    /*
      The  operator for variable defintions allows you to specify a new variable, which can be used later.
      By default you dont have to specify the type of a variable, the compiler is going to figure it for you. 

      Example: 
      foo := (10 + 3)
      
      If you want to explicatly specify the type of a variable you can do this using the following syntax:

      bar: Text = "Hello World"
      
      it differs from the assingment operator which is just a = (equalsign) that it shadows the old variable and its type
    */
    @Getter @Setter @AllArgsConstructor
    public static class VarDef extends Stmt {

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitVarDefStmt(this);
	}

	private final Token name;
	private final Token typeName;
	private final Expr initializer;
    }

    @Getter @Setter @AllArgsConstructor
    public static class Assignment extends Stmt {

	private final Token varName;
	private final Expr value;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitAssignmentStmt(this);
	}
	
    }

    @Getter @Setter @AllArgsConstructor
    public static class While extends Stmt {

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitWhileStmt(this);
	}

	final Expr condition;
	final Stmt body;
    }

    @Getter @Setter @AllArgsConstructor
    public static class Break extends Stmt {

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitBreakStmt(this);
	}

	private final Token location;
    }

    @Getter @Setter @AllArgsConstructor
    public static class Import extends Stmt {

	private final List<Token> libs;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitImportStmt(this);
	}
    }


    public abstract <R> R accept(Visitor<R> visitor);

    @Override
    public String toString() {
	return new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}

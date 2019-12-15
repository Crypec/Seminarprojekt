package util;

import java.util.List;

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
	R visitImportStmt(Import stmt);
    }

    public static class Block extends Stmt {
	Block(List<Stmt> statements) {
	    this.statements = statements;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitBlockStmt(this);
	}
	final List<Stmt> statements;
    }

    public static class Class extends Stmt {

	public static class Attribute {
	    public Token fieldName;
	    public Token typeName;

	    public Attribute(Token fieldName, Token typeName) {
		this.fieldName = fieldName;
		this.typeName = typeName;
	    }
	}

	public Class(Token name, 
		     List<FunctionDecl> methods, List<Attribute> attributes) {

	    this.name = name;
	    this.methods = methods;
	    this.attributes = attributes;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitClassStmt(this);
	}

	public final Token name;
	public final List<Attribute> attributes;
	public final List<FunctionDecl> methods;
    }

    public static class Expression extends Stmt {
	Expression(Expr expression) {
	    this.expression = expression;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitExpressionStmt(this);
	}

	final Expr expression;
    }


    // TODO(Simon): add attribute to check if function is static on type
    public static class FunctionDecl extends Stmt {

	public static class Parameter {
	    public Token varName;
	    public Token typeName;

	    public Parameter(Token varName, Token typeName) {
		this.varName = varName;
		this.typeName = typeName;
	    }
	}
	
	public FunctionDecl(Token name, List<FunctionDecl.Parameter> params, Token returnType, Stmt body) {
	    this.name = name;
	    this.params = params;
	    this.body = body;
	    this.returnType = returnType;
	}
	
	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitFunctionStmt(this);
	}

	public final Token name;
	public final Token returnType;
	public final List<FunctionDecl.Parameter> params;
	public final Stmt body;
    }

    public static class If extends Stmt {
	public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
	    this.condition = condition;
	    this.thenBranch = thenBranch;
	    this.elseBranch = elseBranch;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitIfStmt(this);
	}

	final Expr condition;
	final Stmt thenBranch;
	final Stmt elseBranch;
    }

    public static class Print extends Stmt {
	Print(Expr expression) {
	    this.expression = expression;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitPrintStmt(this);
	}

	final Expr expression;
    }

    public static class Return extends Stmt {
	Return(Token keyword, Expr value) {
	    this.keyword = keyword;
	    this.value = value;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitReturnStmt(this);
	}

	final Token keyword;
	final Expr value;
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
    public static class VarDef extends Stmt {
	public VarDef(Token name, Token typeName, Expr initializer) {
	    this.name = name;
	    this.typeName = typeName;
	    this.initializer = initializer;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitVarDefStmt(this);
	}

	final Token name;
	final Token typeName;
	final Expr initializer;
    }

    public static class Assignment extends Stmt {

	public Assignment(Token name, Expr value) {
	    this.name = name; 
	    this.value = value;
	}

	final Token name;
	final Expr value;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitAssignmentStmt(this);
	}
	
    }

    public static class While extends Stmt {

	public While(Expr condition, Stmt body) {
	    this.condition = condition;
	    this.body = body;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitWhileStmt(this);
	}

	final Expr condition;
	final Stmt body;
    }

    public static class Import extends Stmt {

	List<Token> libs;

	public Import(List<Token> libs) {
	    this.libs = libs;
	}

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

package util;

import java.util.List;

import com.google.gson.*;

public abstract class Stmt {

    interface Visitor<R> {
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

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitBlockStmt(this);
	}
	final List<Stmt> statements;
    }

    public static class Class extends Stmt {

	public static class Attribute {
	    private Token varName;
	    private Token typeName;

	    public Attribute(Token varName, Token typeName) {
		this.varName = varName;
		this.typeName = typeName;
	    }
	}

	public Class(Token name, 
		     List<FunctionDecl> methods, List<Attribute> attributes) {
	    this.name = name;
	    this.methods = methods;
	    this.attributes = attributes;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitClassStmt(this);
	}

	final Token name;
	final List<Attribute> attributes;
	final List<FunctionDecl> methods;
    }

    public static class Expression extends Stmt {
	Expression(Expr expression) {
	    this.expression = expression;
	}

	<R> R accept(Visitor<R> visitor) {
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
	
	public FunctionDecl(Token name, List<FunctionDecl.Parameter> params, Token returnType, List<Stmt> body) {
	    this.name = name;
	    this.params = params;
	    this.body = body;
	    this.returnType = returnType;
	}
	
	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitFunctionStmt(this);
	}

	final Token name;
	final Token returnType;
	final List<FunctionDecl.Parameter> params;
	final List<Stmt> body;
    }

    public static class If extends Stmt {
	public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
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
	Print(Expr expression) {
	    this.expression = expression;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitPrintStmt(this);
	}

	final Expr expression;
    }

    static class Return extends Stmt {
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

    public static class VarDef extends Stmt {
	public VarDef(Token name, Token typeName, Expr initializer) {
	    this.name = name;
	    this.typeName = typeName;
	    this.initializer = initializer;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitVarDefStmt(this);
	}

	final Token name;
	final Token typeName;
	final Expr initializer;
    }

    static class Assignment extends Stmt {

	public Assignment(Token name, Expr value) {
	    this.name = name; 
	    this.value = value;
	}

	final Token name;
	final Expr value;

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitAssignmentStmt(this);
	}
	
    }

    static class While extends Stmt {
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

    public static class Import extends Stmt {

	List<Token> libs;

	public Import(List<Token> libs) {
	    this.libs = libs;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitImportStmt(this);
	}
    }


    abstract <R> R accept(Visitor<R> visitor);

    @Override
    public String toString() {
	return new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}

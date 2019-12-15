package util;

import java.util.List;
import com.google.gson.*;

public abstract class Expr {

    public interface Visitor<R> {
	R visitAssignExpr(Assign expr); // NOTE(Simon): I think this should be a stmt
	R visitBinaryExpr(Binary expr);
	R visitCallExpr(Call expr);
	R visitGetExpr(Get expr);
	R visitGroupingExpr(Grouping expr);
	R visitLiteralExpr(Literal expr);
	R visitLogicalExpr(Logical expr);
	R visitSetExpr(Set expr);
	R visitSuperExpr(Super expr);
	R visitThisExpr(This expr);
	R visitUnaryExpr(Unary expr);
	R visitVariableExpr(Variable expr);
    }

    public static class Assign extends Expr {

	public Token name;
	public Expr value;

	public Token type = null; // use for later in typechecker

	public Assign(Token name, Expr value) {
	    this.name = name;
	    this.value = value;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitAssignExpr(this);
	}
    }

    public static class Binary extends Expr {

	public Expr left;
	public Token operator;
	public Expr right;

	public Binary(Expr left, Token operator, Expr right) {
	    this.left = left;
	    this.operator = operator;
	    this.right = right;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitBinaryExpr(this);
	}
    }

    public static class Call extends Expr {

	public Expr callee;
	public Token paren;
	public List<Expr> arguments;

	public Call(Expr callee, Token paren, List<Expr> arguments) {
	    this.callee = callee;
	    this.paren = paren;
	    this.arguments = arguments;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitCallExpr(this);
	}
    }

    public static class Get extends Expr {

	public Expr object;
	public Token name;

	public Get(Expr object, Token name) {
	    this.object = object;
	    this.name = name;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitGetExpr(this);
	}
    }

    public static class Grouping extends Expr {

	public Expr expression;

	public Grouping(Expr expression) {
	    this.expression = expression;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitGroupingExpr(this);
	}
    }

    public static class Literal extends Expr {

	public Object value;

	public Literal(Object value) {
	    this.value = value;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitLiteralExpr(this);
	}
    }

    public static class Logical extends Expr {

	public Expr left;
	public Token operator;
	public Expr right;

	public Logical(Expr left, Token operator, Expr right) {
	    this.left = left;
	    this.operator = operator;
	    this.right = right;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitLogicalExpr(this);
	}

    }

    public static class Set extends Expr {
	
	public Expr object;
	public Token name;
	public Expr value;

	public Set(Expr object, Token name, Expr value) {
	    this.object = object;
	    this.name = name;
	    this.value = value;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitSetExpr(this);
	}

    }

    public static class Super extends Expr {

	public Token keyword;
	public Token method;

	public Super(Token keyword, Token method) {
	    this.keyword = keyword;
	    this.method = method;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitSuperExpr(this);
	}
    }

    public static class This extends Expr {

	public Token keyword;

	public This(Token keyword) {
	    this.keyword = keyword;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitThisExpr(this);
	}
    }

    public static class Unary extends Expr {

	public Token operator;
	public Expr right;

	public Unary(Token operator, Expr right) {
	    this.operator = operator;
	    this.right = right;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitUnaryExpr(this);
	}
    }

    public static class Variable extends Expr {
	public Token name;

	public Variable(Token name) {
	    this.name = name;
	}

	<R> R accept(Visitor<R> visitor) {
	    return visitor.visitVariableExpr(this);
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

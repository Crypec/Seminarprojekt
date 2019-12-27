package util;

import java.util.List;
import lombok.*;
import com.google.gson.*;

public abstract class Expr {

    public interface Visitor<R> {
	R visitBinaryExpr(Binary expr);
	R visitCallExpr(Call expr);
	R visitGetExpr(Get expr);
	R visitGroupingExpr(Grouping expr);
	R visitLiteralExpr(Literal expr);
	R visitLogicalExpr(Logical expr);
	R visitSetExpr(Set expr);
	R visitThisExpr(This expr);
	R visitUnaryExpr(Unary expr);
	R visitVariableExpr(Variable expr);
	R visitInputExpr(Input expr);
    }

    // public static class Assign extends Expr {

    // 	public Token name;
    // 	public Expr value;

    // 	public Token type = null; // use for later in typechecker

    // 	public Assign(Token name, Expr value) {
    // 	    this.name = name;
    // 	    this.value = value;
    // 	}

    // 	<R> R accept(Visitor<R> visitor) {
    // 	    return visitor.visitAssignExpr(this);
    // 	}
    // }

    @Getter @Setter @AllArgsConstructor
    public static class Binary extends Expr {

	public Expr left;
	public Token operator;
	public Expr right;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitBinaryExpr(this);
	}
    }

    @Getter @Setter @AllArgsConstructor
    public static class Call extends Expr {

	public Expr callee;
	public Token paren;
	public List<Expr> arguments;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitCallExpr(this);
	}
    }


    @Getter @Setter @AllArgsConstructor
    public static class Get extends Expr {

	private Expr object;
	private Token name;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitGetExpr(this);
	}
    }

    @Getter @Setter @AllArgsConstructor
    public static class Grouping extends Expr {

	private final Expr expression;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitGroupingExpr(this);
	}
    }

    @Getter @Setter @AllArgsConstructor
    public static class Literal extends Expr {

	private final Object value;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitLiteralExpr(this);
	}
    }

    @Getter @Setter @AllArgsConstructor
    public static class Logical extends Expr {

	private final Expr left;
	private final Token operator;
	private final Expr right;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitLogicalExpr(this);
	}

    }

    @Getter @Setter @AllArgsConstructor
    public static class Set extends Expr {
	
	private final Expr object;
	private final Token name;
	private final Expr value;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitSetExpr(this);
	}

    }

    @Getter @Setter @AllArgsConstructor
    public static class This extends Expr {

	private final Token keyword;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitThisExpr(this);
	}
    }

    @Getter @Setter @AllArgsConstructor
    public static class Unary extends Expr {

	private final Token operator;
	private final Expr right;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitUnaryExpr(this);
	}
    }

    @Getter @Setter @AllArgsConstructor
    public static class Input extends Expr {

	private final Token message;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitInputExpr(this);
	}
    }

    @Getter @Setter @AllArgsConstructor
    public static class Variable extends Expr {

	private final Token name;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitVariableExpr(this);
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

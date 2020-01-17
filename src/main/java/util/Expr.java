package util;

import java.util.*;
import java.io.Serializable;
import lombok.*;
import com.google.gson.*;

@Getter @Setter
public abstract class Expr implements Serializable {

    public interface Visitor<R> {
	R visitBinaryExpr(Binary expr);
	R visitCallExpr(Call expr);
	R visitGetExpr(Get expr);
	R visitGroupingExpr(Grouping expr);
	R visitLiteralExpr(Literal expr);
	R visitSetExpr(Set expr);
	R visitSelfExpr(Self expr);
	R visitUnaryExpr(Unary expr);
	R visitVariableExpr(Variable expr);
	R visitInputExpr(Input expr);
	R visitAssignExpr(Assign expr);
	R visitStructLiteralExpr(StructLiteral expr);
	R visitArrayAccessExpr(ArrayAccess expr);
    }

    protected TypeInfo type = null; // set type for all expr nodes to null

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Assign extends Expr implements Serializable {

	private final Token name;
	private final Expr value;


	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitAssignExpr(this);
	}
    }

    @Getter @Setter @AllArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class StructLiteral extends Expr implements Serializable {

	public StructLiteral(TypeInfo type, HashMap<Token, Expr> values) {
	    this.type = type;
	    this.values = values;
	}

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitStructLiteralExpr(this);
	}
	private final HashMap<Token, Expr> values;
    }
    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Binary extends Expr implements Serializable {

	private final Expr left;
	private final Token operator;
	private final Expr right;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitBinaryExpr(this);
	}
    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Call extends Expr implements Serializable {

	public final Expr callee;
	public final Token paren;
	public final List<Expr> arguments;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitCallExpr(this);
	}
    }


    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Get extends Expr implements Serializable {

	private final Expr object;
	private final Token name;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitGetExpr(this);
	}
    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Grouping extends Expr implements Serializable {

	private final Expr expression;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitGroupingExpr(this);
	}
    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Literal extends Expr implements Serializable {

	private final Object value;

	
	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitLiteralExpr(this);
	}
    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Set extends Expr implements Serializable {
	
	private final Expr object;
	private final Token name;
	private final Expr value;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitSetExpr(this);
	}

    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Self extends Expr implements Serializable {

	private final Token keyword;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitSelfExpr(this);
	}
    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Unary extends Expr implements Serializable {

	private final Token operator;
	private final Expr right;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitUnaryExpr(this);
	}
    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Input extends Expr implements Serializable {

	private final Expr message;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitInputExpr(this);
	}
    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class Variable extends Expr implements Serializable {

	private final Token name;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitVariableExpr(this);
	}
    }

    @Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode(callSuper=true)
    public static class ArrayAccess extends Expr implements Serializable {

	private final Token name;
	private final Expr index;

	public <R> R accept(Visitor<R> visitor) {
	    return visitor.visitArrayAccessExpr(this);
	}
    }
    public abstract <R> R accept(Visitor<R> visitor);

    @Override
    public String toString() {
	return this.getClass() + new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}

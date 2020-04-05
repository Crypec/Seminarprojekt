package util;

import com.google.common.base.Functions;
import static java.text.MessageFormat.format;
import com.google.common.collect.*;
import com.google.gson.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.*;

public abstract class Stmt implements Serializable {

	public interface Visitor<R> {
		R visitBlockStmt(Block stmt);

		R visitStructDeclStmt(StructDecl stmt);

		R visitExpressionStmt(Expression stmt);

		R visitFunctionStmt(FunctionDecl stmt);

		R visitIfStmt(If stmt);

		R visitPrintStmt(Print stmt);

		R visitReturnStmt(Return stmt);

		R visitVarDefStmt(VarDef stmt);

		R visitWhileStmt(While stmt);

		R visitForStmt(For stmt);

		R visitBreakStmt(Break stmt);

		R visitImportStmt(Import stmt);

		R visitModuleStmt(Module stmt);

		R visitImplBlockStmt(ImplBlock stmt);
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class Module extends Stmt {
		private String moduleName;
		private String filename;
		private List<Stmt> body;

		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitModuleStmt(this);
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class Block extends Stmt implements Serializable {

		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}

		private final List<Stmt> statements;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class StructDecl extends Stmt implements Serializable {

		public String getStringName() {
			return this.name.getLexeme();
		}

		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitStructDeclStmt(this);
		}

		public boolean hasField(String fieldName) {
			return fields != null && fields.containsKey(fieldName);
		}

		public boolean hasMethod(String methodName) {
			if (methods == null) return false;

			return methods.stream()
				.filter(f -> f.getStringName().equals(methodName))
				.findAny()
				.isPresent();
		}

		public TypeInfo getFieldType(String field) {
			return fields.get(field);
		}

		private final Token name;
		private final LinkedHashMap<String, TypeInfo> fields;
		private List<FunctionDecl> methods;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class ImplBlock extends Stmt {

		public <R> R accept(Visitor<R> visitor) {
      return visitor.visitImplBlockStmt(this);
    }

	  public String getStringName() {
		  return this.name.getLexeme();
	  }

	  private final Token name;
    private final List<FunctionDecl> methods;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @EqualsAndHashCode(callSuper = true)
  public static class Expression extends Stmt implements Serializable {

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

	  Expr expression;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @EqualsAndHashCode(callSuper = true)
  public static class FunctionDecl extends Stmt implements Serializable {

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Signature {

		private Token name;
		private final List<FunctionDecl.Signature.Parameter> parameters;
		private TypeInfo returnType;
		private boolean isStatic;

		@Getter
		@Setter
		@AllArgsConstructor
@EqualsAndHashCode
		public static class Parameter {
			private final Token name;
			private final TypeInfo type;

			@Override
			public String toString() {
				return name.getLexeme() + ": " + type;
			}

		}


		public boolean argTypesEqual(Expr.Call call) {
			if (call.getArguments().size() == 0 && this.parameters.size() == 0) return true;

			// TODO(Simon): fixme
			val callArgs = call.getArguments().stream();
			val paramArgs = parameters.stream();

			if (parameters.get(0).getName().getLexeme().equals("selbst")) {

				if ((parameters.size() -1) == 0 && call.getArguments().size() == 0){
					return true;
				}
				return Streams
					.zip(paramArgs.skip(1), callArgs, (a, b) -> a.getType().equals(b.getType()))
					.anyMatch(x -> x);
			}

	return Streams
				.zip(callArgs, paramArgs, (a, b) -> a.getType().equals(b.getType()))
				.anyMatch(x -> x);
		}
		@Override
		public String toString() {
			String paramString = parameters.stream()
				.map(Signature.Parameter::toString)
				.collect(Collectors.joining(","));
			return format("{0}({1} -> {2})", name.getLexeme(), paramString, returnType);
		}
	}

	  public <R> R accept(Visitor<R> visitor) {
		  return visitor.visitFunctionStmt(this);
	  }

	  public String getStringName() {
		  return this.signature.getName().getLexeme();
	  }

	  private final Stmt.FunctionDecl.Signature signature;
	  private final Stmt.Block body;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @EqualsAndHashCode(callSuper = true)
  public static class If extends Stmt implements Serializable {

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Branch {
      private Expr condition;
      private Stmt.Block body;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Stmt.If.Branch primary;
    final List<Stmt.If.Branch> alternatives;
    final Stmt.If.Branch last;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @EqualsAndHashCode(callSuper = true)
  public static class Print extends Stmt implements Serializable {

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    private final Token formatter;
    private final List<Expr> expressions;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @EqualsAndHashCode(callSuper = true)
  public static class Return extends Stmt implements Serializable {

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }
	  private Token location;
	  private Expr value;
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
	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class VarDef extends Stmt implements Serializable {

		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitVarDefStmt(this);
		}

		private List<Token> target;
		private TypeInfo type;
		private final Expr initializer;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class While extends Stmt implements Serializable {

		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}

		final Expr condition;
		final Stmt.Block body;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class For extends Stmt implements Serializable {
		private Expr start;
		private Expr end;

		private Token loopVar;
		private Stmt.Block body;

		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitForStmt(this);
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class Break extends Stmt implements Serializable {

		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBreakStmt(this);
		}

		private final Token location;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class Import extends Stmt implements Serializable {
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

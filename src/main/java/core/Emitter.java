package core;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import lombok.*;
import util.*;

// Transpiles our language to cpp code
// TODO(Simon): replace all usages of String.format because it is slow and not very readable
public class Emitter implements Stmt.Visitor<StringBuilder>, Expr.Visitor<StringBuilder> {

	private static final String lineSeperator = System.getProperty("line.separator");

	public String stringify(List<Stmt> stmts) {
		return stmts
			.stream()
			.map(stmt -> stringify(stmt))
			.filter(Objects::nonNull) // FIXME(Simon): we can probably remove this filter
			.map(StringBuilder::toString)
			.collect(Collectors.joining(lineSeperator));
	}

	public StringBuilder stringify(Expr expr) {
		return expr.accept(this);
	}

	public StringBuilder stringify(Stmt stmt) {
		return stmt.accept(this);
	}

	@Override
	public StringBuilder visitModuleStmt(Stmt.Module ASTNode) {
		return null;
	}

	@Override
	public StringBuilder visitStructDeclStmt(Stmt.StructDecl structDecl) {
		return null;
	}

	public static StringBuilder resolveType(TypeInfo type) {

		if (type instanceof TypeInfo.Array) {
			val array = (TypeInfo.Array) type;
			return new StringBuilder(resolveType(array.getElementType()))
				.append(("[]"));
		} else if (type instanceof TypeInfo.Primitive) {
			val primitive = (TypeInfo.Primitive) type;
			String typeString = switch (primitive.getTypeString()) {
			case "Zahl" -> "int";
			case "Text" -> "char[]";
			case "Bool" -> "boolean";
			default -> primitive.getTypeString();
			};
			return new StringBuilder(typeString);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public StringBuilder visitBlockStmt(Stmt.Block block) {
		val blockStmts = block.getStatements()
			.stream()
			.map(stmt -> stringify(stmt))
			.map(stmt -> lineSeperator + stmt + lineSeperator)
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
		return new StringBuilder()
			.append("{")
			.append(lineSeperator)
			.append(blockStmts)
			.append(lineSeperator)
			.append("}");
	}

	@Override
	public StringBuilder visitExpressionStmt(Stmt.Expression ASTNode) {
		return stringify(ASTNode).append(";");
	}

	@Override
	public StringBuilder visitTupleExpr(Expr.Tuple ASTNode) {
		return null;
	}

	@Override
	public StringBuilder visitArrayLiteralExpr(Expr.ArrayLiteral expr) {
		return null;
	}
	@Override
	public StringBuilder visitModuleAccessExpr(Expr.ModuleAccess expr) {
		return null;
	}

	@Override
	public StringBuilder visitFunctionStmt(Stmt.FunctionDecl func) {
		return new StringBuilder("void ")
			.append(func.getStringName())
			.append(stringify(func.getBody()));
	}

	@Override
	public StringBuilder visitPrintStmt(Stmt.Print printStmt) {
		return null;
	}

	@Override
	public StringBuilder visitIfStmt(Stmt.If stmt) {
		return null;
	}

	@Override
	public StringBuilder visitReturnStmt(Stmt.Return stmt) {
		return null;
	}

	@Override
	public StringBuilder visitVarDefStmt(Stmt.VarDef varDefNode) {

		
		val target = varDefNode.getTarget()
			.stream()
			.map(t -> t.getLexeme())
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

		val buffer = new StringBuilder()
			.append(resolveType(varDefNode.getType()))
			.append(" ")
			.append(target)
			.append(" ")
			.append(stringify(varDefNode.getInitializer()))
			.append(";");
		return buffer;
	}

	@Override
	public StringBuilder visitWhileStmt(Stmt.While whileStmt) {
		return null;
	}

	@Override
	public StringBuilder visitImportStmt(Stmt.Import importStmt) {
		return null;
	}

	@Override
	public StringBuilder visitBreakStmt(Stmt.Break breakStmt) {
		return null;
	}

	@Override
	public StringBuilder visitBinaryExpr(Expr.Binary binary) {
		val left = stringify(binary.getLeft());
		val right = stringify(binary.getRight());
		return new StringBuilder()
			.append(left)
			.append(right);
	}

	@Override
	public StringBuilder visitCallExpr(Expr.Call call) {
		return null;
	}

	@Override
	public StringBuilder visitGetExpr(Expr.Get get) {
		return null;
	}

	@Override
	public StringBuilder visitGroupingExpr(Expr.Grouping grouping) {
		return null;
	}

	@Override
	public StringBuilder visitVariableExpr(Expr.Variable expr) {
		return null;
	}

	@Override
	public StringBuilder visitLiteralExpr(Expr.Literal literal) {
		return new StringBuilder(literal.getValue().toString());
	}

	@Override
	public StringBuilder visitSetExpr(Expr.Set set) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder visitUnaryExpr(Expr.Unary expr) {
		return null;
		// return new StringBuilder()
		// 	.append(expr.getOperatorString())
		// 	.append(emit(expr));
	}

	@Override
	public StringBuilder visitSelfExpr(Expr.Self expr) {
		return null;
	}

	@Override
	public StringBuilder visitInputExpr(Expr.Input expr) {
		return null;
	}

	@Override
	public StringBuilder visitStructLiteralExpr(Expr.StructLiteral literal) {
		return null;
	}

	@Override
	public StringBuilder visitAssignExpr(Expr.Assign expr) {
		return null;
	}

	@Override
	public StringBuilder visitArrayAccessExpr(Expr.ArrayAccess expr) {
		return null;
	}

	@Override
	public StringBuilder visitImplBlockStmt(Stmt.ImplBlock ASTNode) {
		return null;
	}

	@Override
	public StringBuilder visitForStmt(Stmt.For ASTNode) {
		return null;
	}
}

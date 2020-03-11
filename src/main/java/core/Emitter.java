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

	public String emit(List<Stmt> stmts) {
		//stmts.forEach(stmt -> stmt.accept(this));
		return null;
	}

	public StringBuilder visitModuleStmt(Stmt.Module ASTNode) {
		return null;
	}

	@Override
	public StringBuilder visitStructDeclStmt(Stmt.StructDecl structDecl) {
		return null;
	}

	public static StringBuilder jsonSerializerFunc(Stmt.StructDecl node) {

		return null;
	}

	public static StringBuilder resolveType(TypeInfo type) {
		// return switch (type.getTypeStringBuilder()) {
		// case "Zahl": yield "double";
		// case "Text": yield "std::string";
		// case "Bool": yield "boolean";
		// case "#Null": yield "null";
		// default: yield type.getTypeStringBuilder();
		// };
		return null;
	}

	@Override
	public StringBuilder visitBlockStmt(Stmt.Block block) {
		return null;
	}

	@Override
	public StringBuilder visitExpressionStmt(Stmt.Expression ASTNode) {
		return null;
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
		return null;
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
	public StringBuilder visitVarDefStmt(Stmt.VarDef varDef) {
		return null;
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
		return null;
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
		return null;
	}

	@Override
	public StringBuilder visitSetExpr(Expr.Set set) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder visitUnaryExpr(Expr.Unary expr) {
		return null;
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

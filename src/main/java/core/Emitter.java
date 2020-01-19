package core;

import util.*;
import lombok.*;
import java.util.*;
import java.util.stream.*;


// Transpiles our language to cpp code
// TODO(Simon): replace all usages of String.format because it is slow and not very readable
public class Emitter implements Stmt.Visitor<String>, Expr.Visitor<String> {
    
    public String emit(List<Stmt> stmts) {

		var prelude = App.readFileToString("./prelude.cpp");

		return prelude + stmts.stream()
			.filter(stmt -> stmt != null)
			.map(stmt -> stmt.accept(this))
			.collect(Collectors.joining("\n\n")) + "\nint main() {\n\tStart();\n}" ;
    }

    public String visitModuleStmt(Stmt.Module ASTNode) {
		return null;
    }

    @Override
    public String visitStructDeclStmt(Stmt.StructDecl structDecl) {
	String members = structDecl.getMembers().stream()
	    .map(member -> String.format("    %s %s;", resolveType(member.getType()), member.getName().getLexeme()))
	    .collect(Collectors.joining("\n"));

	return String.format("struct %s {%n%s %s %n};", structDecl.getName().getLexeme(), members, emitJsonSerialization(structDecl));
    }

	public static String emitJsonSerialization(Stmt.StructDecl structDecl) {
		String structName = structDecl.getName().getLexeme();
		String paramName = String.format("__param__%s", structName);
		var sb = new StringBuilder();
		for (val member : structDecl.getMembers()) {
			sb.append(String.format("""
									{"%s", %s.%s}
									""",
									member.getName(),
									paramName,
									member.getName()));
		}
		return String.format("void to_json(json& j, const %s&) {%s}", structName, paramName, sb.toString());

	}

	public static String resolveType(TypeInfo type) {
		return switch (type.getType()) {
		case "Zahl": yield "double";
		case "Text": yield "std::string";
		case "Bool": yield "boolean";
		case "#Null": yield "null";
		default: yield type.getType() ;
		};
    }
    
    @Override
    public String visitBlockStmt(Stmt.Block block) {
	return "{" + block.getStatements().stream()
	    .map(stmt -> stmt.accept(this))
	    .map(stmt -> String.format("%n%s%n", stmt))
	    .collect(Collectors.joining())
	    .concat("}");
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression ASTNode) {
	return String.format("%s;", ASTNode.getExpression().accept(this));
    }

    @Override
    public String visitFunctionStmt(Stmt.FunctionDecl func) {

	String params = func.getParameters()
	    .stream()
	    .map(param -> String.format("%s &%s", resolveType(param.getType()), param.getName().getLexeme())) // TODO(Simon): getType().getType() :D
		.collect(Collectors.joining(","));

	String returnType = func.getReturnType() == null ? "void" : resolveType(func.getReturnType());
	var body = visitBlockStmt(func.getBody());

	return String.format("%s %s(%s) %s", returnType, func.getName().getLexeme(), params, body);
    }

    @Override
    public String visitPrintStmt(Stmt.Print printStmt) {

	String args = printStmt
	    .getExpressions()
	    .stream()
	    .map(expr -> expr.accept(this))
	    .collect(Collectors.joining(", "));

	if (args == null || args.isEmpty()) {
	    return  String.format("fmt::print(%s);", printStmt.getFormatter().getLexeme());
	} else {
	    return String.format("fmt::print(%s,%s);", printStmt.getFormatter().getLexeme(), args);
	}
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {

	String primaryCondition = stmt.getPrimary().getCondition().accept(this);
	String primaryBlock = visitBlockStmt(stmt.getPrimary().getBody());
	String primary = String.format("if (%s) %s", primaryCondition, primaryBlock);

	// String elseBranches = stmt.getAlternatives().stream()
	// 	.filter(branch -> branch != null)
	//     .map(branch -> String.format("else if (%s) %s", branch.getCondition().accept(this), visitBlockStmt(branch.getBody())))
	//     .collect(Collectors.joining(" "));
	// String finalBranch = "";
	// if (stmt.getLast() != null) {
	// 	finalBranch = String.format(" else %s", stmt.getLast().getBody().accept(this));
	// }
	
	return new StringBuilder()
	    .append(primary)
	    .append("")
	    .append("")
	    .toString();
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
	return String.format("return %s;", stmt.getValue().accept(this));
    }

    @Override
    public String visitVarDefStmt(Stmt.VarDef varDef) {
		// TODO(Simon): Use own infered type!!!
		String varType = varDef.getType() == null? "auto": resolveType(varDef.getType());
		var expr = varDef.getInitializer().accept(this);
		return String.format("%s %s = %s;", varType, varDef.getName().getLexeme(), expr);
    }

    @Override
    public String visitWhileStmt(Stmt.While whileStmt) {
		var block = visitBlockStmt(whileStmt.getBody());
		return String.format("while (%s) %s", whileStmt.getCondition().accept(this), block);
    }

    @Override
    public String visitImportStmt(Stmt.Import importStmt) {

		String alwaysImport = """
			#include <vector>
			#include <iostream>
			#include <fmt/format.h>
			#include <fmt/printf.h>
			""";

		return String.format("%s%n", alwaysImport);
    }

    @Override
    public String visitBreakStmt(Stmt.Break breakStmt) {
	return "break;";
    }

    @Override
    public String visitBinaryExpr(Expr.Binary binary) {
	return String.format("%s %s %s", binary.getLeft().accept(this) , binary.getOperator().getLexeme(), binary.getRight().accept(this));
    }

    @Override
    public String visitCallExpr(Expr.Call call) {

	var callee = call.getCallee().accept(this);

	var sb = new StringBuilder();
	for (var arg : call.getArguments()) {
	    sb.append(arg.accept(this));
	    sb.append(", ");
	}
	if (sb.length() > 0) sb.setLength(sb.length() -2); //remove trailing comma

	return String.format("%s(%s)", callee, sb.toString());
    }

    @Override
    public String visitGetExpr(Expr.Get get) {
	var expr  = get.getObject().accept(this);
	return String.format("%s.%s", expr, get.getName().getLexeme());
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping grouping) {
	return String.format("(%s)", grouping.getExpression().accept(this));
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
		return expr.getName().getLexeme();
    }

    @Override
    public String visitLiteralExpr(Expr.Literal literal) {
	if (literal.getValue() == null) return "null";
	return literal.getValue().toString();
    }

    @Override
    public String visitSetExpr(Expr.Set set) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
	return String.format("%s %s", expr.getOperator().getLexeme(), expr.accept(this));
    }

    @Override
    public String visitSelfExpr(Expr.Self expr) {
	return "this";
    }

    @Override
    public String visitInputExpr(Expr.Input expr) {
		return String.format("%s(%s)",CppPrelude.input, expr.getMessage().accept(this));
	}

    @Override
    public String visitStructLiteralExpr(Expr.StructLiteral literal) {
	return String.format("new %s()", literal.getType());
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
	var value = expr.getValue().accept(this);
	return String.format("%s = %s;", expr.getName().getLexeme(), value);
    }

    @Override
    public String visitArrayAccessExpr(Expr.ArrayAccess expr) {
	return String.format("%s[%s]", expr.getName().getLexeme(), expr.getIndex().accept(this));
    }

    @Override
    public String visitImplBlockStmt(Stmt.ImplBlock ASTNode) {
	return null;
    }
}

package core;

import util.*;
import java.util.*;


// Transpiles our language to cpp code
public class Emitter implements Stmt.Visitor<String>, Expr.Visitor<String> {

    public String emit(List<Stmt> stmts) {
	var sb = new StringBuilder();

	// NOTE(Simon): I dont know if we really need this or if we can just transpile to a single cpp file
	//sb.append("\n#pragma once\n");
	for (var stmt : stmts) {
	    sb.append(stmt.accept(this));
	    sb.append("\n\n");
	}
	return sb.toString();
    }

    @Override
    public String visitClassStmt(Stmt.Class classStmt) {

	var sb = new StringBuilder();

	for (var attrb : classStmt.getAttributes()) {
	    String type = resolveType(attrb.getTypeName());
	    String def = String.format("    %s %s;%n", type, attrb.getFieldName().getLexeme());
	    sb.append(def);
	}

	return String.format("struct %s {%n%s};", classStmt.getName().getLexeme(), sb.toString());
    }

    public static String resolveType(Token type) {
	return switch (type.getLexeme()) {
	case "Zahl": yield "double";
	case "Text": yield "std::string";
	case "Bool": yield "boolean";
	case "#Null": yield "null";
	default: yield type.getLexeme();
	};
    }
    
    @Override
    public String visitBlockStmt(Stmt.Block block) {
	var sb = new StringBuilder("{");
	for (var stmt : block.getStatements()) {
	    sb.append("\n");
	    sb.append("\t");
	    sb.append(stmt.accept(this));
	}
	sb.append("\n}");
	return sb.toString();
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression expr) {
	return null;
    }

    @Override
    public String visitFunctionStmt(Stmt.FunctionDecl func) {

	var sb = new StringBuilder();
	for (var param : func.getParams()) {
	    var cppType = resolveType(param.getTypeName());
	    sb.append(String.format("%s &%s, ", cppType, param.getVarName().getLexeme()));
	}
	// remove trailing comma for last function paramater
	if (sb.length() > 1) sb.setLength(sb.length() -2);

	String returnTypeCpp = func.getReturnType() == null ? "void" : resolveType(func.getReturnType());

	var block = visitBlockStmt(func.getBody());

	return String.format("%s %s(%s) %s", returnTypeCpp, func.getName().getLexeme(), sb.toString(), block);
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {

	var args = new StringBuilder();
	for (var arg : stmt.getExpressions()) {
	    args.append(", ");
	    args.append(arg.accept(this));
	}

	return String.format("fmt::print(%s%s);", stmt.getFormatter().getLexeme(), args.toString());
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
	String body = visitBlockStmt(stmt.getBody());
	return String.format("if (%s) %s", stmt.getCondition().accept(this), body);

    }
    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
	return String.format("return %s;", stmt.value.accept(this));
    }

    @Override
    public String visitVarDefStmt(Stmt.VarDef varDef) {

	String varType = varDef.getTypeName() == null? "auto": varDef.getTypeName().getLexeme();
	var expr = varDef.getInitializer().accept(this);
	return String.format("%s %s = %s;", varType, varDef.getName().getLexeme(), expr);
    }

    @Override
    public String visitAssignmentStmt(Stmt.Assignment stmt) {
	return String.format("%s = %s;", stmt.getVarName().getLexeme(), stmt.getValue().accept(this));
    }

    @Override
    public String visitWhileStmt(Stmt.While whileStmt) {
	return null;
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
	return null;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary binary) {
	return String.format("%s %s %s", binary.left.accept(this) , binary.operator.getLexeme(), binary.right.accept(this));
    }

    @Override
    public String visitCallExpr(Expr.Call call) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String visitGetExpr(Expr.Get get) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping grouping) {
	return String.format("(%s)", grouping.getExpression().accept(this));
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String visitLiteralExpr(Expr.Literal literal) {
	if (literal.getValue() == null) return "null";
	return literal.getValue().toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical logical) {
	throw new UnsupportedOperationException();
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
    public String visitThisExpr(Expr.This expr) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String visitInputExpr(Expr.Input expr) {
	return null;
    }
}

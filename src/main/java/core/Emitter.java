package core;

import util.*;
import java.io.*;
import lombok.*;
import java.util.*;
import java.util.stream.*;


// Transpiles our language to cpp code
// TODO(Simon): replace all usages of String.format because it is slow and not very readable
public class Emitter implements Stmt.Visitor<String>, Expr.Visitor<String> {

	private static final String lineSeperator = System.getProperty("line.separator");
	
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

		String path = "cpp_build/src/types/" + structDecl.getName().getLexeme();

		var sb = new StringBuffer().append(String.format("struct %s {%n%s%n};", structDecl.getName().getLexeme(), members))
			.append(lineSeperator)
			.append(jsonSerializerFunc(structDecl))
			.append(lineSeperator)
			.append(buildFormatterTemplate(structDecl))
			.toString();

		try (var out = new PrintWriter(path)) {
out.print(""); // clear file
		out.println(sb.toString());
	} catch(Exception e) {
		e.printStackTrace();
	}
		return null;
	}

	public static String jsonSerializerFunc(Stmt.StructDecl node) {

		String paramName = "__param__" + node.getName().getLexeme();
		var sb = new StringBuffer();
		for (val member : node.getMembers()) {
			String memberName = member.getName().getLexeme();
			String template = """
				{"%s", %s.%s},
				""";
			sb.append(String.format(template, memberName, paramName, memberName));
		}
		String structName = node.getName().getLexeme();
		String template = """
			void to_json(json& j, const %s& %s) {
			j = json{%s};
		}
		""";
		return String.format(template, structName, paramName, sb);
	}

	public static String buildFormatterTemplate(Stmt.StructDecl node) {
		String template = """
			template <>
			struct fmt::formatter<%s> {
			constexpr auto parse(format_parse_context& ctx) { return ctx.begin(); }

			template <typename FormatContext>
			auto format(const %s& %s, FormatContext& ctx) {
				json j = %s;
				return format_to(ctx.out(), "{}", j.dump(4));
			}
		};
		""";
		String paramName = "__param__" + node.getName().getLexeme();
		String structName = node.getName().getLexeme();
		return String.format(template, structName, structName, paramName, paramName);
	}

	public static String resolveType(TypeInfo type) {
		return switch (type.getTypeString()) {
		case "Zahl": yield "double";
		case "Text": yield "std::string";
		case "Bool": yield "boolean";
		case "#Null": yield "null";
		default: yield type.getTypeString();
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

		String params = func.getSignature().getParameters()
			.stream()
			.map(param -> String.format("%s &%s", resolveType(param.getType()), param.getName().getLexeme())) // TODO(Simon): getType().getType() :D
			.collect(Collectors.joining(","));

		String returnType = func.getSignature().getReturnType() == null ? "void" : resolveType(func.getSignature().getReturnType());
		var body = visitBlockStmt(func.getBody());

		return String.format("%s %s(%s) %s", returnType, func.getSignature().getName().getLexeme(), params, body);
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

		/* TODO(Simon):
		 * Right now order of declaration in the structliteral matters and has  to be the same as in the structdecl. 
		 * This should not be the case, to fix this we could either generate a constructor for the class and math the
		 * index of the struct against the value at the index of the structliteral, or we generate a builder for
		 *  every struct in the programm, to set all the values in the fields with a setter method.
		 */
		
		System.out.println(literal.getType().getTypeString());
		var sb = new StringBuffer();
		for (val entry : literal.getValues()) {
			sb.append(String.format(".%s = %s, ", entry.getFieldName().getLexeme(), entry.getValue().accept(this)));
		}
		if (sb.length() > 0) sb.setLength(sb.length() - 1); // remove trailing comma
		return String.format("%s {%s}", literal.getType().getTypeString(), sb);
	}

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
	var value = expr.getValue().accept(this);
	return String.format("%s = %s;", expr.getName().getLexeme(), value);
    }

    @Override
    public String visitArrayAccessExpr(Expr.ArrayAccess expr) {
		return String.format("%s[(int)%s]", expr.getName().getLexeme(), expr.getIndex().accept(this));
    }

    @Override
    public String visitImplBlockStmt(Stmt.ImplBlock ASTNode) {
	return null;
    }
}

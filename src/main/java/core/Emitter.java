package core;

import util.*;


// Transpiles our language to cpp code
public class Emitter implements Stmt.Visitor<String> {

    public String emit(Stmt stmt) {
	return stmt.accept(this);
    }

    @Override
    public String visitClassStmt(Stmt.Class classStmt) {

	var sb = new StringBuilder();

	for (var attrb : classStmt.attributes) {

	    String type = convertType(attrb.typeName);

	    String def = String.format("    %s %s;%n", type, attrb.fieldName.getLexeme());
	    sb.append(def);
	}

	return String.format("struct %s {%n%s};", classStmt.name.getLexeme(), sb.toString());
    }

    public static String convertType(Token type) {
	return switch (type.getLexeme()) {
	case "Zahl": yield "double";
	case "Text": yield "std::string";
	case "Bool": yield "boolean";
	default: yield type.getLexeme();
	};
    }
    

    public String visitBlockStmt(Stmt.Block block) {
	return null;
    }

    public String visitExpressionStmt(Stmt.Expression expr) {
	return null;
    }

    public String visitFunctionStmt(Stmt.FunctionDecl func) {
	return null;
    }

    public String visitPrintStmt(Stmt.Print printStmt) {
	return null;
    }

    public String visitIfStmt(Stmt.If ifStmt) {
	return null;
    }

    public String visitReturnStmt(Stmt.Return returnStmt) {
	return null;
    }

    public String visitVarDefStmt(Stmt.VarDef varDef) {
	return null;
    }

    public String visitAssignmentStmt(Stmt.Assignment assignment) {
	return null;
    }

    public String visitWhileStmt(Stmt.While whileStmt) {
	return null;
    }

    public String visitImportStmt(Stmt.Import importStmt) {
	return null;
    }

    public String visitInputStmt(Stmt.Import input) {
	return null;
    }
}
    

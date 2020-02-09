package core;

import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.*;
import util.*;

public class Typer implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

	private Map<String, List<Stmt.FunctionDecl>> functionTable = new HashMap();
	private Map<String, Stmt.StructDecl> typeTable = new HashMap<String, Stmt.StructDecl>();
	private Deque<HashMap<String, TypeInfo>> context = new ArrayDeque<HashMap<String, TypeInfo>>();

	public void inferTypes(@NonNull Expr node) {
		node.accept(this);
	}

	public void infer(List<Stmt> ast) {
		val _typeTable = ast.stream()
			.filter(Stmt.StructDecl.class::isInstance)
			.map(Stmt.StructDecl.class::cast)
			.collect(Collectors.toMap(x -> x.getName().getLexeme(), Function.identity()));
		this.typeTable = new HashMap<String, Stmt.StructDecl>(_typeTable); 
		this.functionTable = makeFunctionTable(ast);

		// context for global variables
		this.context.push(new HashMap<String, TypeInfo>());
		ast.stream().filter(Objects::nonNull).forEach(node -> node.accept(this));
	}

	public static HashMap<String, List<Stmt.FunctionDecl>> makeFunctionTable(List<Stmt> ast) {
		var functionTable = new HashMap<String, List<Stmt.FunctionDecl>>();
		for (val stmt : ast) {
			if (stmt instanceof Stmt.FunctionDecl) {
				val funcDecl = (Stmt.FunctionDecl) stmt;
				final String funcName = funcDecl.getSignature().getName().getLexeme();
				functionTable.putIfAbsent(funcName, new ArrayList<Stmt.FunctionDecl>());
				functionTable.get(funcName).add(funcDecl);
			}
		}
		return functionTable;
	}
		

	@Override
	public Void visitImplBlockStmt(@NonNull Stmt.ImplBlock node) {
		return null;
	}

	@Override
	public Void visitModuleStmt(@NonNull Stmt.Module node) {
		return null;
	}

	@Override
	public Void visitImportStmt(@NonNull Stmt.Import node) {
		//NOTE(Simon): these should be resolved allready in the parser
		return null;
	}
	@Override
	public Void visitBreakStmt(@NonNull Stmt.Break node) {
		return null;
	}

	@Override
	public Void visitWhileStmt(@NonNull Stmt.While node) {
		return null;
	}

	@Override
	public Void visitVarDefStmt(Stmt.VarDef node) {
		if (node == null) return null;
		inferTypes(node.getInitializer());

		Optional<TypeInfo> inferredType = node.getInitializer().getType();
		if (inferredType.isEmpty()) {
			Report.builder()
				.wasFatal(true)
				.errType("Invalide Zuweisung")
				.errMsg("Der Datentyp einer Zuweisung kann niemanls null sein!")
				.token(node.getTarget().get(0))
				.url("TODO")
				.build()
				.print();
			node.setType(Optional.of(
					TypeInfo.builder()
					.isDirty(true)
					.typeString("")
					.arrayLevel(0)
					.location(null)
					.build()));
			return null;
		}

		Optional<TypeInfo> specifiedType = node.getType();

		if (specifiedType.isPresent()) {
			//NOTE(Simon): type is allready specified by the user and matches the inferred type by the compiler
			//TODO(Simon): NOCHECKING
			// checkType(inferredType.get(), specifiedType.get(), "Benutzerdefinierter Wert der Variable entspricht nicht dem Wert des initalisierungsausdrucks"); 
		} else {
			node.setType(inferredType);
		}

		String name = node.getTarget().get(0).getLexeme();
		this.context.getFirst().put(name, inferredType.get());
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return node) {
		return null;
	}

	@Override
	public Void visitPrintStmt(Stmt.Print node) {
		return null;
	}

	@Override
	public Void visitIfStmt(Stmt.If node) {
		if (node == null) return null;
		inferTypes(node.getPrimary().getCondition());
		checkType(node.getPrimary().getCondition().getType().get(), TypeInfo.BOOLEANTYPE, "Conditionen einer 'wenn' verzweigung muessen immer den Wert Bool haben");
		return null;
	}

	@Override
	public Void visitFunctionStmt(@NonNull Stmt.FunctionDecl node) {
		val returnType = node.getSignature().getReturnType();
		if (returnType.isPresent()) {
			inferBlock(node.getBody(), returnType.get());
		} else {
			inferBlock(node.getBody());
		}
		return null;
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression node) {
		if (node == null) return null;
		inferTypes(node.getExpression());
		return null;
	}
 
	@Override
	public Void visitStructDeclStmt(Stmt.StructDecl node) {
		return null;
	}

	private void inferBlock(Stmt.Block block) {
		if (context.isEmpty()) {
			this.context.push(new HashMap<String, TypeInfo>());
		} else {
			this.context.push(new HashMap<String, TypeInfo>(this.context.getFirst()));
		}
		block.getStatements().stream().filter(Objects::nonNull).forEach(stmt -> stmt.accept(this));
		this.context.pop();
	}

	public void inferBlock(Stmt.Block block, TypeInfo returnType) {
		if (context.isEmpty()) {
			this.context.push(new HashMap<String, TypeInfo>());
		} else {
			this.context.push(new HashMap<String, TypeInfo>(this.context.getFirst()));
		}
		block.getStatements().stream()
			.filter(Objects::nonNull)
			.filter(Stmt.Return.class::isInstance)
			.map(Stmt.Return.class::cast)
			.forEach(stmt -> {
					inferTypes(stmt.getValue());
					checkType(stmt.getValue().getType().get(), returnType, "Rueckgabetyp in Funktionskoerper entspricht nicht dem Rueckgabetyp welcher in der Funktionssignatur angegeben ist!", stmt.getLocation());
				});
		block.getStatements().stream().filter(Objects::nonNull).forEach(stmt -> stmt.accept(this));
		this.context.pop();
	
	}

	@Override
	public Void visitBlockStmt(Stmt.Block node) {
		inferBlock(node);
		return null;
	}


	@Override
	public Void visitArrayAccessExpr(Expr.ArrayAccess expr) {
        return null;
    }

    @Override
    public Void visitStructLiteralExpr(Expr.StructLiteral node) {
		return null;
	}

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        return null;
    }

    @Override
    public Void visitInputExpr(Expr.Input expr) {
		val type = Optional
				.of(
					TypeInfo.builder()
					.typeString(TypeInfo.STRINGTYPE)
					.arrayLevel(0)
					.isDirty(false)
					.location(null)
					.build());
		expr.setType(type);
		return null;
	}

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {

		if (this.context.isEmpty()) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(String.format("Kein Kontext fuer die Variable %s definiert", expr.getName().getLexeme()))
				.url("TODO")
				.build()
				.print();
			expr.setType(Optional.of(
					TypeInfo.builder()
					.isDirty(true)
					.typeString("")
					.arrayLevel(0)
					.location(null)
					.build()));
			return null;
		}
		if (!this.context.getFirst().containsKey(expr.getName().getLexeme())) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(String.format("Die Variable %s ist im aktuellen Kontext nicht definiert", expr.getName().getLexeme()))
					.example("""
							 a := 20;
							 {
								 b := a;  // In Ordung
							 }
						 """)
					.example("""
							 {
								 a := 20;
							 } // hier "stirbt" da der Kontet in dem es definiert wurde endet
							 b := a; // Nicht in Ordung da, nicht im aktuellen Kontext definiert ist
						 """)
				.url("TODO")
				.build()
				.print();
			expr.setType(Optional.empty());
			return null;
		}

		val type = context.getLast().get(expr.getName().getLexeme());
		expr.setType(Optional.of(type));
		return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
		expr.getRight().accept(this);
		checkType(expr.getType().get(), expr.getOperator());
		return null;
	}

    @Override
    public Void visitSelfExpr(Expr.Self expr) {
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        return null;
     }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
		if (expr == null) return null;
		var value = expr.getValue();
		var baseType = TypeInfo.builder()
			.typeString("TEMPLATE")
			.arrayLevel(0)
			.isDirty(false)
			.location(null)
			.build();
		if (value instanceof Double) {
			baseType.setTypeString(TypeInfo.NUMBERTYPE);
		} else if (value instanceof String) {
			baseType.setTypeString(TypeInfo.STRINGTYPE);
			baseType.setArrayLevel(1); // allow indexing of strings with [] operator
		} else if (value instanceof Boolean) {
			baseType.setTypeString(TypeInfo.BOOLEANTYPE);
		} else {
			expr.setType(Optional.empty());
			System.out.println("Internal Error you should never see this :: type error for literal");
			return null;
		}
		expr.setType(Optional.of(baseType));
		return null;
    }

    @Override
    public Void visitGroupingExpr(@NonNull Expr.Grouping expr) {
		expr.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visitCallExpr(@NonNull Expr.Call callExpr) {
		callExpr.getArguments().stream().filter(Objects::nonNull).forEach(x -> inferTypes(x));

		String name = ((Expr.Variable)callExpr.getCallee()).getName().getLexeme();
		if (!functionTable.containsKey(name)) {
			//TODO(Simon): check for functions with similar names
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(String.format("Keine Funktion mit dem Namen %s gefunden", name))
				.url("TODO")
				.build()
				.print();
			callExpr.getType().get().setIsDirty(true);
			return null;
		}

		val func = functionTable.get(name);
		val test = new ArrayList();
		
		val matchingFunctionSignatures = functionTable.get(name).stream()
			.map(Stmt.FunctionDecl::getSignature)
			.filter(x -> x.argTypesEqual(callExpr))
			.collect(Collectors.toList());
	System.out.println();
		// more than one functiondecl with the same overload does not make sense
		if (matchingFunctionSignatures.size() > 1) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(String.format("Fur den Funktionsaufruf der Funktion %s haben gibt es mehrere Funktionen mit der gleichen Signatur", name))
				.url("TODO function overloading")
				.build()
				.print();
			callExpr.setType(Optional.of(
					TypeInfo.builder()
							.typeString("")
							.arrayLevel(0)
							.isDirty(true)
							.location(null)
					.build()));
			return null;
		} else if (matchingFunctionSignatures.size() > 1) {
			Report.builder().wasFatal(true).errType("Typenfehler")
					.errMsg(String.format(
							"Fur den Funktionsaufruf der Funktion %s haben wir keine passende ueberladung gefunden",
							name))
					.url("TODO function overloading").build().print();
			callExpr.setType(
					Optional.of(TypeInfo.builder().typeString("").arrayLevel(0).isDirty(true).location(null).build()));
			return null;
		}
 		callExpr.setType(matchingFunctionSignatures.get(0).getReturnType());
		return null;
    }

	@Override
	public Void visitBinaryExpr(@NonNull Expr.Binary expr) {
		expr.getLeft().accept(this);
        expr.getRight().accept(this);

		val dirtyType = TypeInfo.builder()
			.isDirty(true)
			.typeString("")
			.arrayLevel(0)
			.location(null)
			.build();
		val leftType = expr.getLeft().getType().orElse(dirtyType);
		val rightType = expr.getRight().getType().orElse(dirtyType);

		if (leftType.getIsDirty() || rightType.getIsDirty()) {
			expr.setType(Optional.of(dirtyType));
			return null;
		}
		expr.setType(Optional.of(checkType(leftType, rightType, expr.getOperator(), String.format("Folgende Operation: %s %s %s ist nicht erlaubt", leftType, expr.getOperator().getLexeme(), rightType))));
		return null;
    }

	public static void checkType(TypeInfo type, Token operator) {
		if (!TypeInfo.isAllowed(type, operator)) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(String.format("Der Typ: %s ist fuer folgenden Operator unzulaessig: %s", type.getTypeString(), operator))
				.token(operator)
				.url("TODO")
				.build()
				.print();
		}
	}

	public static void checkType(TypeInfo actual, String expected, String errMsg) {
		if (!actual.getTypeString().equals(expected) || actual.getArrayLevel() != 0) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(errMsg)
				.url("TODO")
				.build()
				.print();
		}
	}

	public static TypeInfo checkType(TypeInfo actual, TypeInfo expected, Token operator, String errMsg) {
		if (actual.getArrayLevel() != 0 || expected.getArrayLevel() != 0) {
			Report.builder().wasFatal(true).errType("Typenfehler").errMsg(errMsg).url("TODO").build().print();
			actual.setIsDirty(true);
			return actual;
		}
		if (!actual.getTypeString().equals(expected.getTypeString())) {
			Report.builder().wasFatal(true).errType("Typenfehler").errMsg(errMsg).url("TODO").build().print();
			actual.setIsDirty(true);
		}
		if (!TypeInfo.isAllowed(actual, operator)) {
			Report.builder().wasFatal(true).errType("Typenfehler").errMsg(errMsg).url("TODO").build().print();
			actual.setIsDirty(true);
		}
		return actual;
	}

	public static TypeInfo checkType(TypeInfo actual, TypeInfo expected, String errMsg, Token location) {
		if (actual.getArrayLevel() != 0 || expected.getArrayLevel() != 0) {
			Report.builder().wasFatal(true).errType("Typenfehler").errMsg(errMsg).url("TODO").token(location).build().print();
			actual.setIsDirty(true);
			return actual;
		}
		if (!actual.getTypeString().equals(expected.getTypeString())) {
			Report.builder().wasFatal(true).errType("Typenfehler").errMsg(errMsg).url("TODO").token(location).build().print();
			actual.setIsDirty(true);
		}
		return actual;
	}
}

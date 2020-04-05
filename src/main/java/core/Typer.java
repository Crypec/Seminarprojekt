package core;

import static java.text.MessageFormat.format;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.*;
import util.*;

public class Typer implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

	private Map<String, List<Stmt.FunctionDecl>> functionTable;
	private Map<String, Stmt.StructDecl> typeTable;
	private Deque<Map<String, TypeInfo>> contextStack;

	public void inferType(Expr node) {
		node.accept(this);
	}

	public void infer(@NonNull List<Stmt> ast) {
		reorderASTImplBlocks(ast);
		this.typeTable = makeTypeTable(ast);
		this.functionTable = makeFunctionTable(ast);

		makeNewContext(); // context for global variables
		ast.stream().filter(Objects::nonNull).forEach(node -> node.accept(this));
	}

	public static HashMap<String, List<Stmt.FunctionDecl>>
		makeFunctionTable(List<Stmt> ast) {
		val _functionTable = ast.stream()
			.filter(Stmt.FunctionDecl.class ::isInstance)
			.map(Stmt.FunctionDecl.class ::cast)
			.collect(Collectors.groupingBy(Stmt.FunctionDecl::getStringName));
		return new HashMap<String, List<Stmt.FunctionDecl>>(_functionTable);
	}

	private static void reorderASTImplBlocks(List<Stmt> ast) {
		var implBlocks =
			ast.stream()
            .filter(Stmt.ImplBlock.class ::isInstance)
            .map(Stmt.ImplBlock.class ::cast)
            .collect(Collectors.toMap(Stmt.ImplBlock::getStringName, Stmt.ImplBlock::getMethods));
		val structDecls =
			ast.stream()
			.filter(Stmt.StructDecl.class ::isInstance)
			.map(Stmt.StructDecl.class ::cast)
			.collect(Collectors.toList());
		val sDecls = new HashMap<String, Stmt.StructDecl>();
		for (val structDecl : structDecls) {
			if (sDecls.containsKey(structDecl.getStringName())) {
				Report.builder()
					.wasFatal(true)
					.errType("Typenfehler")
					.errMsg(format("Der Datentyp {0} ist mehrfach definiert", structDecl.getStringName()))
					.token(structDecl.getName())
					.url("TODO")
					.build()
					.print();
				break;
			}
			sDecls.put(structDecl.getStringName(), structDecl);
		}
		// TODO(Simon): validate impl block types
		implBlocks.entrySet().stream()
			.filter(e -> sDecls.containsKey(e.getKey()))
			.forEach(e -> sDecls.get(e.getKey()).setMethods(e.getValue()));
	}
	private static HashMap<String, Stmt.StructDecl>
		makeTypeTable(List<Stmt> ast) {
		val _typeTable =
			ast.stream()
            .filter(Stmt.StructDecl.class ::isInstance)
            .map(Stmt.StructDecl.class ::cast)
			.collect(Collectors.toMap(
									  Stmt.StructDecl::getStringName, Function.identity(),
									  (first, second) -> {
										  // if we find a structdecl which is already definied we just ignore the second implementation
										  return first;
									  }));
		// NOTE(Simon): primitive types supported by the compiler
		// FIXME(Simon): if this does not seem right, it might because is isn't, you should fix it :c
		_typeTable.put(TypeInfo.STRINGTYPE, new Stmt.StructDecl(new Token(TokenType.IDEN, TypeInfo.STRINGTYPE, null, null, 0, 0, 0), null, null));
		_typeTable.put(TypeInfo.NUMBERTYPE, new Stmt.StructDecl(new Token(TokenType.IDEN, TypeInfo.NUMBERTYPE, null, null, 0, 0, 0), null, null));
		_typeTable.put(TypeInfo.BOOLEANTYPE, new Stmt.StructDecl(new Token(TokenType.IDEN, TypeInfo.BOOLEANTYPE, null, null, 0, 0, 0), null, null));
		_typeTable.put(TypeInfo.VOIDTYPE, new Stmt.StructDecl(new Token(TokenType.IDEN, TypeInfo.VOIDTYPE, null, null, 0, 0, 0), null, null));
		return new HashMap<String, Stmt.StructDecl>(_typeTable);
	}

	@Override
	public Void visitImplBlockStmt(@NonNull Stmt.ImplBlock node) {
		final String name = node.getName().getLexeme();
		if (!typeTable.containsKey(name)) {
			Report.builder()
				.wasFatal(true)
				.errType("Invalider Implementierungsblock")
				.errMsg(format("Keinen DatenTyp mit dem namen {0} gefunden", name))
				.token(node.getName())
				.url("TODO")
				.build()
				.print();
		}
		node.getMethods().stream().forEach(m -> m.accept(this));
		return null;
	}

	@Override
	public Void visitModuleStmt(@NonNull Stmt.Module node) {
		return null;
	}

	@Override
	public Void visitImportStmt(@NonNull Stmt.Import node) {
		// NOTE(Simon): these should be resolved allready in the parser
		return null;
	}
	@Override
	public Void visitBreakStmt(@NonNull Stmt.Break node) {
		return null;
	}

	@Override
	public Void visitWhileStmt(@NonNull Stmt.While node) {
		inferType(node.getCondition());
		val type = node.getCondition().getType();
		checkType(
				  type, TypeInfo.BOOLEANTYPE,
				  format(
						 "Die Kondition einer waehrend schleife muss immer den Typ Bool haben, bei dir hat sie allerdings folgenden Typ: {0}",
						 type));
		return null;
	}

	public Void visitForStmt(@NonNull Stmt.For node) { return null; }

	@Override
	public Void visitVarDefStmt(@NonNull Stmt.VarDef node) {
		inferType(node.getInitializer());
		val inferredType = node.getInitializer().getType();

		// if (inferredType == null) {
		// 	Report.builder()
		// 		.wasFatal(true)
		// 		.errType("Invalide Zuweisung")
		// 		.errMsg("Der Datentyp einer Zuweisung kann niemanls () sein!")
		// 		.token(node.getTarget().get(0))
		// 		.url("TODO")
		// 		.build()
		// 		.print();
		// 	node.setType(TypeInfo.voidType(null));
		// 	return null;
		// }

		val specifiedType = node.getType();

		if (specifiedType != null) {
			// NOTE(Simon): type is allready specified by the user and matches the
			// inferred type by the compiler
			// TODO(Simon): NOCHECKING
			checkTypeEqual(
						   inferredType, specifiedType,
						   "Benutzerdefinierter Wert der Variable entspricht nicht dem Wert des initalisierungsausdrucks", inferredType.getLocation());
		} else {
			node.setType(inferredType);
		}

		String name = node.getTarget().get(0).getLexeme();
		contextInsert(name, inferredType);
		this.contextStack.getFirst().put(name, inferredType);
		return null;
	}

	@Override
	public Void visitReturnStmt(@NonNull Stmt.Return node) {
		// NOTE(Simon): we check all returns in a separate pass while visiting a
		// block
		if (node.getValue() != null) {
			inferType(node.getValue());
		}
		return null;
	}

	@Override
	public Void visitPrintStmt(@NonNull Stmt.Print node) {
		return null;
	}

	@Override
	public Void visitIfStmt(@NonNull Stmt.If node) {
		inferType(node.getPrimary().getCondition());
		inferBlock(node.getPrimary().getBody(), null, null);
		checkType(
				  node.getPrimary().getCondition().getType(), TypeInfo.BOOLEANTYPE,
				  "Bedinung einer 'wenn' verzweigung muessen immer den Datentyp Bool haben");
		return null;
	}

	@Override
	public Void visitFunctionStmt(@NonNull Stmt.FunctionDecl node) {
		val returnType = node.getSignature().getReturnType();
		validateTypeExisting(returnType);
		val params = node.getSignature().getParameters();
		params.stream()
			.map(Stmt.FunctionDecl.Signature.Parameter::getType)
			.forEach(t -> validateTypeExisting(t));
		inferBlock(node.getBody(), returnType, params);
		return null;
	}

	@Override
	public Void visitExpressionStmt(@NonNull Stmt.Expression node) {
		inferType(node.getExpression());
		return null;
	}

	@Override
	public Void visitStructDeclStmt(@NonNull Stmt.StructDecl node) {
		node.getFields()
			.values()
			.forEach(t -> validateTypeExisting(t));
		return null;
	}

	public void inferBlock(@NonNull Stmt.Block block, TypeInfo returnType,
						   List<Stmt.FunctionDecl.Signature.Parameter> params) {

		makeNewContext();

		if (params != null) {
			params.stream().forEach(
									p
									-> this.contextStack.getFirst().putIfAbsent(p.getName().getLexeme(), p.getType()));
		}
		block.getStatements()
			.stream()
			.filter(Objects::nonNull)
				.forEach(stmt -> {
						if (stmt instanceof Stmt.Block) {
							Stmt.Block b = (Stmt.Block) stmt;
							inferBlock(b, returnType, params); // params could be replaced with null
						} else {
							stmt.accept(this);
						}
					});
		checkReturnTypes(block, returnType);
		destroyContext();
	}

	private void makeNewContext() {
		if (this.contextStack == null) {
			this.contextStack = new ArrayDeque(); // TODO(Simon): Provide actual type information
		}

		if (contextStack.isEmpty()) {
			this.contextStack.push(new HashMap<String, TypeInfo>());
		} else {
			this.contextStack.push(new HashMap<String, TypeInfo>(this.contextStack.peekFirst()));
		}
	}

	private void destroyContext() { this.contextStack.pop(); }

	private void contextInsert(String varname, TypeInfo type) {
		this.contextStack.getFirst().put(varname, type);
	}

	private boolean contextContains(String varname) {
		return this.contextStack.getFirst().containsKey(varname);
	}

	private TypeInfo contextTypeGet(String varname) {
		return this.contextStack.getFirst().get(varname);
	}


	public void checkReturnTypes(Stmt.Block block, TypeInfo returnType) {
		block.getStatements()
			.stream()
			.filter(Objects::nonNull)
			.filter(Stmt.Return.class::isInstance)
			.map(Stmt.Return.class::cast)
			.filter(r -> r.getValue() != null) // FIXME(Simon): we should really have somehting like an empty expression, instead of null
			.forEach(r -> {
					inferType(r.getValue());
					var exprType = r.getValue().getType();
					String errMsg = format("Rueckgabetyp in Funktionskoerper entspricht nicht dem Rueckgabetyp welcher in der Funktionssignatur angegeben ist! :: {0} -> {1}",
										   exprType, returnType);
					checkTypeEqual(exprType, returnType, errMsg, r.getLocation());
				});
	}

	@Override
	public Void visitBlockStmt(@NonNull Stmt.Block node) {
		// NOTE(Simon): Unfortunely with the visitor pattern we cant pass returnype and params easly around without conecting all the nodes in the AST, so we visit all blocks manually 
		//inferBlock(node, null, null);
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(Expr.ArrayAccess expr) {

		inferType(expr.getCallee());
		inferType(expr.getIndex());
		checkType(expr.getIndex().getType(), TypeInfo.NUMBERTYPE, "Array indizies muessen immer den Datentyp Zahl haben");

		val type = expr.getType();
		if (type instanceof TypeInfo.Array) {
			val arrayElemType = ((TypeInfo.Array) type).getElementType();
			expr.setType(arrayElemType);
		} else if (type instanceof TypeInfo.Primitive && type.getBaseTypeString().equals(TypeInfo.STRINGTYPE)) {
			// allow user to use square brackets syntax for accessing strings
		} else {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg("Die [index] Operation ist fuer den Datentyp: {0} night erlaubt")
				.token(type.getLocation())
				.build()
				.print();
			return null;
		}
		return null;
	}

	@Override
	public Void visitStructLiteralExpr(Expr.StructLiteral node) {
		String literalName = node.getStringName();
		if (!typeTable.containsKey(literalName)) {
			String errMsg = format("Wir haben keinen DatenTyp mit dem Namen {0} gefunden", literalName);
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(errMsg)
				.token(node.getType().getLocation())
				.build()
				.print();
			return null;
		}

		node.getValues().forEach(field -> inferType(field.getValue()));
		val structDecl = typeTable.get(literalName);
		for (val field : node.getValues()) {
			String fieldName = field.getStringName();
			if (structDecl.hasField(fieldName)) {
				String errMsg = format("Der Datentyp {0} des Objektliterals fuer {1} entspricht nicht {2}, wie in der Deklaration angegeben", fieldName, literalName, structDecl.getFieldType(fieldName));
				checkTypeEqual(structDecl.getFieldType(fieldName), field.getValue().getType(), errMsg, field.getName());
			} else {
				String errMsg = format("Der Datentyp {0}: hat kein feld mit dem Namen {1}", literalName, fieldName);
				Report.builder()
					.wasFatal(true)
					.errType("Typenfehler")
					.errMsg(errMsg)
					.token(field.getName())
					.build()
					.print();
			}
		}

		// TODO(Simon): this needs to be cleaned up
		Map<String, Boolean> visited = structDecl.getFields().keySet().stream()
			.collect(Collectors.toMap(Function.identity() , x -> false));
		node.getValues().stream()
			.forEach(v -> visited.put(v.getStringName(), true));
		visited.entrySet()
			.stream()
			.filter(e -> !e.getValue())
			.forEach(e -> {
					String errMsg = format("Du scheinst dem feld {0} im Objektliteral fuer {1} keinen Wert zugewiesen zu haben", e.getKey(), literalName);
					Report.builder()
						.wasFatal(true)
						.errType("Typenfehler")
						.errMsg(errMsg)
						.token(node.getType().getLocation())
						.url("TODO")
						.build()
						.print();
				});
		return null;
	}

	@Override
	public Void visitArrayLiteralExpr(Expr.ArrayLiteral node) {
		node.getElements().stream().forEach(x -> inferType(x));
		// TODO(Simon): make sure all elems of the array are equal
		if (node.getElements().size() <= 0) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg("Ein Feldliteral muss immer mindestens 1 element enthalten")
				.token(node.getLocation())
				.build()
				.print();
			return null;
		}

		

		// HACK(Simon): check that optional typeinfo is present
		var elemType = node.getElements().get(0).getType();
		var arrayType = TypeInfo.Array
			.builder()
			.elementType(elemType)
			.location(node.getLocation())
			.build();
		node.setType(arrayType);
		return null;
	}
	@Override
	public Void visitTupleExpr(Expr.Tuple node) {
		node.getElements().forEach(expr -> inferType(expr));
		val elemTypes = node.getElements().stream()
			.map(Expr::getType)
			.collect(Collectors.toCollection(ArrayList::new));
		var type = TypeInfo.Tuple.builder()
			.elementTypes(elemTypes)
			.location(node.getLocation())
			.build();
		node.setType(type);
		return null;
	}

	@Override
	public Void visitAssignExpr(Expr.Assign expr) {
		return null;
	}

	@Override
	public Void visitInputExpr(Expr.Input expr) {
		val type = TypeInfo.Primitive.builder()
			.typeString(TypeInfo.STRINGTYPE)
			.location(null)
			.build();
		expr.setType(type);
		return null;
	}

	@Override
	public Void visitVariableExpr(Expr.Variable expr) {
		if (this.contextStack.isEmpty()) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(format("Kein Kontext fuer die Variable {0} definiert",
							   expr.getName().getLexeme()))
				.url("TODO")
				.build()
				.print();
			expr.setType(TypeInfo.voidType(expr.getName()));
			return null;
		}
		if (!contextContains(expr.getName().getLexeme())) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(format("Die Variable {0} ist im aktuellen Kontext nicht definiert", expr.getName().getLexeme()))
				.example("""
						 a := 20;
						 {
							 b:
							 = a; // In Ordung
						 }
						 """)
				.example("""
						 {
							 a:
							 = 20;
						 } // hier "stirbt" da der Kontet in dem es definiert wurde endet
				b := a; // Nicht in Ordung da, nicht im aktuellen Kontext definiert ist
						 """)
				.url("TODO")
				.build()
				.print();
		expr.setType(TypeInfo.voidType(expr.getName())); 
		return null;
		}
		val type = contextStack.getFirst().get(expr.getName().getLexeme());
						 expr.setType(type);
						 return null;
	}

	@Override
		public Void visitUnaryExpr(Expr.Unary expr) {
		expr.getRight().accept(this);
		checkType(expr.getType(), expr.getOperator());
		return null;
	}

	@Override
	public Void visitSelfExpr(Expr.Self expr) {
		// FIXME(Simon): this is horrible, we are relying on the fact that the parser inserts a desugared variant of the self param in the context of the functionDecl!!!
		if (!contextContains("selbst")) {
			Report.builder()
				.wasFatal(true)
				.errType("TypenFehler")
				.errMsg("selbst darfst du nur in Operationen, also Funktionen die auf Datentypen wirken verweden, die nicht als statisch makiert sind")
				.token(expr.getKeyword())
				.url("TODO")
				.build()
				.print();
			expr.setType(TypeInfo.voidType(expr.getKeyword()));
			return null;
		} 
		expr.setType(contextTypeGet("selbst"));
		return null;
	}

	@Override
		public Void visitSetExpr(Expr.Set expr) {
		return null;
	}

	@Override
	public Void visitGetExpr(Expr.Get expr) {
		inferType(expr.getObject());

		final String baseTypeString = expr.getObject().getType().getBaseTypeString();
		final String fieldName = expr.getPropertyName().getLexeme();

		val structDecl = typeTable.get(baseTypeString);
		if (structDecl.hasField(fieldName)) {
			expr.setType(structDecl.getFieldType(fieldName));
		} else {
			expr.setType(expr.getObject().getType());
		}
		return null;
	}

	@Override
	public Void visitModuleAccessExpr(Expr.ModuleAccess expr) {
		return null;
	}

	@Override
			public Void visitLiteralExpr(@NonNull Expr.Literal expr) {
			val value = expr.getValue();
			String typeString;
			if (value instanceof Double) {
				typeString = TypeInfo.NUMBERTYPE;
			} else if (value instanceof String) {
				typeString = TypeInfo.STRINGTYPE;
			} else if (value instanceof Boolean) {
				typeString = TypeInfo.BOOLEANTYPE;
			} else {
				throw new IllegalArgumentException("literal expr is of unknown type: " + expr.getValue().getClass());
			}
			val type = TypeInfo.Primitive
.builder()
			.typeString(typeString)
			.location(expr.getLocation())
			.build();
expr.setType(type);
	return null;
  }

  @Override
  public Void visitGroupingExpr(@NonNull Expr.Grouping expr) {
	  expr.getExpression().accept(this);
    return null;
  }

  @Override
  public Void visitCallExpr(@NonNull Expr.Call callExpr) {
	  callExpr.getArguments().stream().forEach(arg -> inferType(arg));
	  Token calleeName = getCalleeName(callExpr.getCallee());

	  val functionCandidates = getPossibleOverloads(callExpr);
	  
	  // more than one functiondecl with the same overload does not make sense
	   if (functionCandidates.isEmpty()) {
		   Report.builder()
			  .wasFatal(true)
			  .errType("Typenfehler")
			  .errMsg(format("Fuer den Funktionsaufruf der Funktion {0} haben wir keine passende Ueberladung gefunden",
							 calleeName.getLexeme()))
			  .url("TODO function overloading")
			  .token(calleeName)
			  .build()
			  .print();
		  callExpr.setType(TypeInfo.voidType(calleeName));
		  return null;

	   } else if (functionCandidates.get().size() > 1) {
		   Report.builder()
			   .wasFatal(true)
			   .errType("Typenfehler")
			   .errMsg(format("Fur den Funktionsaufruf der Funktion {0} haben gibt es mehrere Funktionen mit der gleichen Signatur",
							  calleeName.getLexeme()))
			   .url("TODO function overloading")
			   .token(calleeName)
			   .build()
			   .print();
		   callExpr.setType(TypeInfo.voidType(calleeName));
		   return null;
	   }
	   callExpr.setType(functionCandidates.get().get(0).getReturnType());
	   return null;
  }

	private Optional<List<Stmt.FunctionDecl.Signature>> getPossibleOverloads(@NonNull Expr.Call callExpr) {

		Token functionName = getCalleeName(callExpr.getCallee());
		List<Stmt.FunctionDecl> possibleMatches;

		if (callExpr.getCallee() instanceof Expr.Get) {
			inferType(callExpr.getCallee());

			val callee = (Expr.Get) callExpr.getCallee();
			val structDecl = typeTable.get(callExpr.getCallee().getType().getBaseTypeString());

			if (structDecl.hasMethod(functionName.getLexeme())) {
				possibleMatches = structDecl.getMethods()
					.stream()
					.filter(f -> f.getStringName().equals(functionName.getLexeme()))
					.collect(Collectors.toList());
			} else {
				Report.builder()
					.wasFatal(true)
					.errType("Typenfehler")
					.errMsg(format("Keine Method mit dem Namen: {0}() fuer den Datentyp: {1} definiert", functionName.getLexeme(), structDecl.getStringName()))
					.token(functionName)
					.url("TODO")
					.build()
					.print();
				return Optional.empty();
			}
		} else if (callExpr.getCallee() instanceof Expr.Variable) {
			System.out.println("[Variable] Function Call");
			val callee = (Expr.Variable) callExpr.getCallee();
			if (functionTable.containsKey(functionName.getLexeme())) {
				possibleMatches = functionTable.get(functionName.getLexeme());
			} else {
				// TODO(Simon): check for functions with similar names
				Report.builder()
					.wasFatal(true)
					.errType("Typenfehler")
					.errMsg(format("Keine Funktion mit dem Namen {0} gefunden",
								   functionName.getLexeme()))
					.url("TODO")
					.token(functionName)
					.build()
					.print();
				return Optional.empty();
			}
		} else if (callExpr.getCallee() instanceof Expr.ModuleAccess) {
			inferType(callExpr.getCallee());
			System.out.println("[MODULE] Function Call");
			val callee = (Expr.ModuleAccess) callExpr.getCallee();
			val calleeName = callee.getAccessChain().get(callee.getAccessChain().size() -2);
			if (!typeIsKnown(calleeName.getLexeme())) {
				return Optional.empty();
			}
			Stmt.StructDecl structDecl = typeTable.get(calleeName.getLexeme());
			if (structDecl.hasMethod(functionName.getLexeme())) {
				possibleMatches = structDecl.getMethods()
					.stream()
					.filter(f -> f.getStringName().equals(functionName.getLexeme()))
					.collect(Collectors.toList());
			} else {
				Report.builder()
					.wasFatal(true)
					.errType("Typenfehler")
					.errMsg(format("Keine Method mit dem Namen: {0}() fuer den Datentyp: {1} definiert", functionName.getLexeme(), structDecl.getStringName()))
					.token(functionName)
					.url("TODO")
					.build()
					.print();
				return Optional.empty();
			}
		}
		else {
			throw new IllegalArgumentException("Expr callee is of unknown type: " + callExpr.getCallee().getClass());
		}
		return Optional.of(possibleMatches.stream()
						   .map(Stmt.FunctionDecl::getSignature)
						   .filter(x -> x.argTypesEqual(callExpr))
						   .collect(Collectors.toList()));
	}

	private static Token getCalleeName(Expr expr) {
		// TODO(Simon): this can be improved with the new if pattern matching of
		// java 14
		if (expr instanceof Expr.Variable) {
			return ((Expr.Variable)expr).getName();
		} else if (expr instanceof Expr.Get) {
			return ((Expr.Get)expr).getPropertyName();
		} else if (expr instanceof Expr.ModuleAccess) {
			// FIXME(Simon)
			val accessChain = ((Expr.ModuleAccess) expr).getAccessChain();
			return accessChain.get(accessChain.size() -1);
		}
					throw new IllegalArgumentException("Can't get callee name for class of type: " + expr.getClass());
				}

  @Override
	  public Void visitBinaryExpr(@NonNull Expr.Binary expr) {

	  inferType(expr.getLeft());
	  inferType(expr.getRight());

	  val leftType = expr.getLeft().getType();
	  val rightType = expr.getRight().getType();

	  String errMsg = format("Folgende Operation: {0} {1} {2} ist nicht erlaubt",
							 leftType, expr.getOperator().getLexeme(), rightType);
	  var combinedType =
		  checkType(leftType, rightType, expr.getOperator(), errMsg);
	  expr.setType(combinedType);
	  return null;
  }

  public static void checkType(TypeInfo type, Token operator) {
	  if (!TypeInfo.isAllowed(type, operator)) {
		  Report.builder()
			  .wasFatal(true)
			  .errType("Typenfehler")
			  .errMsg(format("Der Typ: {0} ist fuer folgenden Operator unzulaessig: {1}", type,
							 operator))
			  .token(operator)
			  .url("TODO")
			  .build()
			  .print();
	  }
  }

  public static TypeInfo checkType(TypeInfo lhs, TypeInfo rhs, Token operator, String errMsg) {
	  val reduced = TypeInfo.reduce(lhs, rhs, operator);
	  if (reduced.isPresent()) {
		  return reduced.get();
	  } else {
		  Report.builder()
			  .wasFatal(true)
			  .errType("Typenfehler")
			  .errMsg(errMsg)
			  .token(operator)
			  .url("TODO")
			  .build()
			  .print();
		return TypeInfo.voidType(operator);
	  }
  }

	public static TypeInfo checkTypeEqual(TypeInfo actual, TypeInfo expected,
										  String errMsg, Token location) {

		if (!actual.equals(expected)) {
			Report.builder()
				.wasFatal(true)
				.errType("Typenfehler")
				.errMsg(errMsg)
				.url("TODO")
				.token(location)
				.build()
				.print();
		}
		return expected;
  }

  public static void checkType(TypeInfo actual, String expected,
							   String errMsg) {
	  if (!actual.getBaseTypeString().equals(expected)) {
		  Report.builder()
			  .wasFatal(true)
			  .errType("Typenfehler")
			  .errMsg(errMsg)
			  .token(actual.getLocation())
			  .url("TODO")
			  .build()
			  .print();
	  }
	}

	private boolean typeIsKnown(String typeString) {
		return this.typeTable.containsKey(typeString);
	}

	public void validateTypeExisting(TypeInfo type) {
		if (type instanceof TypeInfo.Tuple) {
			val tuple = (TypeInfo.Tuple) type;
			tuple.getElementTypes().forEach(elemType -> validateTypeExisting(elemType));
		} else {
			if (!typeIsKnown(type.getBaseTypeString())) {
				Report.builder()
					.wasFatal(true)
					.errType("Typenfehler")
					.errMsg(format("Kein DatenTyp mit dem Namen {0} gefunden", type.getBaseTypeString()))
					.url("TODO")
					.token(type.getLocation())
					.build()
					.print();
			}
		}
	}

	// public static void checkType(TypeInfo actual, TypeInfo expected,
	//                              String errMsg) {
	//   if (!actual.equals(expected)) {
	//     Report.builder()
	//         .wasFatal(true)
	//         .errType("Typenfehler")
	//         .errMsg(errMsg)
	//         .token(actual.getLocation())
	//         .url("TODO")
	//         .build()
	//         .print();
	//   }
	// }

	public static Object memClone(Object object) {
		try {
			var outputStream = new ByteArrayOutputStream();
			var outputStrm = new ObjectOutputStream(outputStream);
			outputStrm.writeObject(object);
			var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			var objInputStream = new ObjectInputStream(inputStream);
			return objInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

package core;

import java.util.*;
import util.*;
import com.github.tomaslanger.chalk.*;

// TODO(Simon): make err messages better and more descriptive. Maybe we should handle more cases with their own err msg
public class Parser extends Iter<Token> {

    public Parser(Token[] tokenStream) {
		super(tokenStream); 
    }

    public List<Stmt> parse() {
        var ast = new ArrayList();
        if (check(TokenType.IMPORT)) {
            var libs = parseImport();
            ast.add(libs);  // TODO(Simon): Resolve libs and external files
		}
        while (this.hasNext())   {
            var stmt = parseStmt();
            ast.add(stmt);
        } 
        return ast;
    }

    public void syncParserState() {
		while (hasNext()) {
			switch (peek().getType()) {
			case FUNCTION, CLASS, IMPL, IMPORT: return;
				// case IDEN: {
				// 	if (checkNext(TokenType.COLON)) return;
				// 	if (checkNext(TokenType.VARDEF)) return;
				// }
			default: next();
			}
		}
    }

    public Stmt parseStmt() {
		try {
			while (hasNext() && !check(TokenType.EOF)) {
				return switch (peek().getType()) {
				case CLASS: yield parseStructDecl();
				case FUNCTION: yield parseFunctionDecl(false);
				case IMPORT: yield parseImport();
				case IMPL: yield parseImplBlock();
				case IDEN: {
					if (checkNext(TokenType.VARDEF)) yield parseVarDef();
					else yield parseExprStmt();
				}
				default: {
					Report.builder()
						.wasFatal(true)
						.errType("Unerwarteter Token")
						.errMsg("An dieser Stelle haben wir einen der folgenden Token erwartet: #benutze, fun, Typ, Vertrag, Enum, impl")
						.token(peek())
						.url("TODO.de")
						.build()
						.print();
					yield null; // unreachable because we throw an exception
				}
				};
			}
		} catch(Report.Error e) {
			syncParserState();
		}
		return null;
    }

    public Stmt.FunctionDecl parseFunctionDecl(boolean selfParamAllowed) {

		var err = Report.builder()
			.wasFatal(true)
			.errType("Fehler beim parsen einer Funktion")
			.example("fun addiere(a: Zahl, b: Zahl) -> Zahl {}")
			.example("fun summiere(a: [Zahl]) -> Zahl")
			.example("fun darf_auto_fahren(person: Person) -> Bool")
			.url("TODO") 
			.build();
		consume(TokenType.FUNCTION, "Eine Funktionsdeclaration beginnt immmer mit dem fun schluesselwort", err);

		Token functionName = consume(TokenType.IDEN, err);
		consume(TokenType.LPAREN, "Nach dem Namen der Funktion folgen 0 oder mehr parameter welche die Funktion entgegen nimmt", err);

		var params = new ArrayList();
		if (selfParamAllowed && check(TokenType.SELF)) {
			var location = consume(TokenType.SELF, "Der Parameter selbst darf nur als Parameter zu Methoden eines Datentypes vorkommen, er erlaubt dir die Werte des DatenTypes auf den die Methode agiert zu aendern",  err);
			consume(TokenType.COMMA, "An dieser Stelle haben wir ein Komma erwartet, um die Parameter in der Funktionssignatur voneinander zu trennen", err);
			params.add(new Stmt.FunctionDecl.Parameter(location, null));
		}

		while (!check(TokenType.RPAREN)) {
			Token paramName = consume(TokenType.IDEN, "An dieser Stelle haben wir den Namen des Parameters erwartet", err);
			consume(TokenType.COLON, "An dieser Stelle haben wir einen Doppelpunkt : erwartet, er trennt den Parameternamen von dessen DatenTyp", err);
			var paramType = parseTypeSpecifier();
			params.add(new Stmt.FunctionDecl.Parameter(paramName, paramType));
			if (check(TokenType.RPAREN)) break;
			consume(TokenType.COMMA, "An dieser Stelle haben wir ein Komma erwartet, um die einzelnen Parameter in der Funktionssignatur voneinander zu trennen", err);
		}
		consume(TokenType.RPAREN, "An dieser Stelle haben wir eine schliessende Klammer: ) erwartet", err);

		TypeInfo returnType = null;
		if (!check(TokenType.STARTBLOCK)) {
			consume(TokenType.ARROW, "wenn deine Funktion einen Wert zurueckgeben soll, musst du den DatenTypes nach einem Pfeil -> angeben", err);
			returnType = parseTypeSpecifier();
		}
		var body = parseBlock();
		return new Stmt.FunctionDecl(functionName, params, returnType, body);
    }

	public TypeInfo parseTypeSpecifier() {
		var err = Report.builder()
			.wasFatal(true)
			.errType("TypenFehler")
			.url("www.TODO.de")
			.example("a: Zahl = 42; // hier ist a von Typ Zahl")
			.example("fun foo(a: Zahl) // hier nimmt die Funktion foo einen Parameter vom Typ Zahl")
			.example("fun foo(a: [Text]) // hier nimmt die Funktion foo einen Parameter vom Typ Feld von Zahl")
			.build();

		int arrayLevel = 0; 
		while (matchAny(TokenType.LBRACKET)) {
			arrayLevel += 1;
		}
		var typeName = consume(TokenType.IDEN, "An dieser Stelle haben wir den Namen eines DatenTypen erwartet", err);

		for (int i = 0; i < arrayLevel; i++) {
			consume(TokenType.RBRACKET, "An dieser Stelle haben wir eine schliessende eckige Klammer erwartet: ], es scheint als haettest du mehr oeffnende Klammern wie schliessende", err);
		}
		return new TypeInfo(typeName.getLexeme(), typeName, arrayLevel);
	}

	public Stmt.ImplBlock parseImplBlock() {
		var err = Report.builder()
			.wasFatal(true)
			.errType("Fehler beim parsen eines Implementationsblocks")
			.example("""
					 impl Person {
						 fun neu() -> Person {}
					 }
					 """)
			.example("""
					 impl Auto {
						 fun fahre_los(selbst) {}
						 fun tank_ist_voll() -> Bool {}
						 fun darf_fahren(selbst, person: Person) -> Bool {}
					 }
					 """)
			.example("""
					 impl Form fuer Kreis {
						 fun flaeche() -> Zahl {}
					 }
					 """)
			.token(peek())
			.url("TODO")
			.build();
					 consume(TokenType.IMPL, err);
					 var name = consume(TokenType.IDEN, "Name des Impl blocks erwartet", err);
					 consume(TokenType.STARTBLOCK, "An dieser Stelle haben wir eine oeffnende Klammer erwartet: {", err);
					 var methods = new ArrayList();
					 while (!check(TokenType.ENDBLOCK)) {
						 methods.add(parseFunctionDecl(true));
					 }
					 consume(TokenType.ENDBLOCK, err);
					 return new Stmt.ImplBlock(name, methods);
	}

    public Stmt.Block parseBlock() {

		var err = Report.builder()
			.wasFatal(true)
			.errType("Block nicht begrenzt")
			.url("www.TODO.de")
			.build();

		consume(TokenType.STARTBLOCK, "An dieser Stelle haben wir eine oeffnende Klammer erwartet: {", err);

		var stmts = new ArrayList();

		while (!check(TokenType.ENDBLOCK)) {
			var stmt = switch (peek().getType()) {
			case IDEN: {
				if (checkNext(TokenType.VARDEF)) yield parseVarDef();
				if (checkNext(TokenType.COLON)) yield parseVarDef();
				else yield parseExprStmt();
			}
			case IF: yield parseIf();
			case FOR: yield parseForLoop();
			case WHILE: yield parseWhileLoop();
			case PRINT: yield parsePrint();
			case RETURN: yield parseReturn();
			case BREAK: yield parseBreak();
			default: {
				Report.builder()
					.wasFatal(true)
					.errType("Unerwarteter Token")
					.errMsg("An dieser Stelle haben wir einen der folgenden Token erwartet: wenn, fuer, solange, #ausgabe, #eingabe, rueckgabe, stop, iden")
					.token(peek())
					.url("www.TODO.de")
					.build()
					.print();
				yield null;
			}
			};
			if (stmt != null) stmts.add(stmt);
		}
		consume(TokenType.ENDBLOCK, "An dieser Stelle haben wir einen schliessende Klammer erwartet: }", err);
		return new Stmt.Block(stmts);
    }

    public Stmt.Break parseBreak() {
		var err = Report.builder()
			.wasFatal(true)
			.errType("Fehler beim parsen eines stop stmts")
			.errType("Der Stopp befehl erlaubt dir eine Schleife zu verlassen")
			.url("www.TODO.de")
			.build();
		var location = consume(TokenType.BREAK, err);
		consume(TokenType.SEMICOLON, "Semicolon erwartet", err);
		return new Stmt.Break(location);
    }

    public Expr.Input parseInput() {
		var err = Report.builder()
			.wasFatal(true)
			.errType("Fehler beim parsen eines Eingabe befehls")
			.url("www.TODO.de")
			.build();

		consume(TokenType.READINPUT, "An dieser Stelle haben wir eigentlich einen #eingabe befehl erwartet", err);
		consume(TokenType.LPAREN, "Auch wenn der #eingabe befehl eine compiler interne Funktion ist, wie du an dem # vor dem Namen erkennen kannst, wird er wie eine Normale Funktion aufgerufen, an dieser Stelle haben wir eine oeffnende Klamme: ( erwartet", err);
		var exprMsg = parseExpr();
		consume(TokenType.RPAREN, "An dieser Stelle haben wir eine schliessende Klammer: ) erwartet", err);
		return new Expr.Input(exprMsg);
    }

	public Stmt.Print parsePrint() {
		var err = Report.builder()
			.wasFatal(true)
			.errType("Fehler beim parsen eines Printbefehls")
			.url("www.TODO.de")
			.build();
	
		consume(TokenType.PRINT, "An dieser Stelle haben wir den #ausgabe Befehl erwartet", err);
		consume(TokenType.LPAREN, "Auch wenn der #ausgabe befehl eine compiler interne Funktion ist, wie du an dem # vor dem Namen erkennen kannst, wird er wie eine Normale Funktion aufgerufen, an dieser Stelle haben wir eine oeffnende Klamme: ( erwartet", err);

		Token formatter = null;
		if (check(TokenType.STRINGLITERAL)) {
			formatter = consume(TokenType.STRINGLITERAL, "du scheinst die formatangabe fuer den ausgabe befehl vergessen zu haben.", err); 
			matchAny(TokenType.COMMA);
		}

		var exprs = new ArrayList();
		while (!matchAny(TokenType.RPAREN)) {
			exprs.add(parseExpr());
			matchAny(TokenType.COMMA);
		}
		consume(TokenType.SEMICOLON, "Nach einem ausgabe ausdruck haben wir ein Semicolon erwartet!", err);

		return new Stmt.Print(formatter, exprs);
    }

    public Stmt.While parseWhileLoop() {

		var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen einer Solange schleife")
            .example("solange antwort != 42 {}")
            .url("TODO.de")
            .build();
		consume(TokenType.WHILE, "An dieser stelle haben wir das solange schluesselwort erwartet", err);

		var condion = parseExpr();
		var body = parseBlock();
		return new Stmt.While(condion, body);
    }

    public Stmt.Return parseReturn() {

		var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen eines rueckgabe Befehls")
			.example("rueckgabe;")
			.example("rueckgabe 4 + 2;")
			.example("rueckgabe 2 * 3.14159 * r;")
			.url("TODO.de")
			.build();

		var location = consume(TokenType.RETURN, "An dieser Stelle haben wir einen rueckgabe befehl erwartet", err);
		Expr expr = parseExpr();
		consume(TokenType.SEMICOLON, "An dieser Stelle haben wir ein Semicolon erwartet", err);
		return new Stmt.Return(location, expr);
    }

    /*
      import example:

      #benutze [
      "Mathe",
      "Fmt",
      "Zahl",
      "Test"
      ]

      We use them to dertermine which files to parse next.
    */
    public Stmt.Import parseImport() {

		var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen eines #benutze Befehls, wir sind uns nicht sicher welche datein du meinst")
            .example("""
					 #benutze [
							   "Basis",
							   "Mathe",
							   ]
					 """)
            .url("TODO.de")
            .build();
		consume(TokenType.IMPORT, "An dieser Stelle haben wir einen #benutze befehl erwartet", err);

		var libs = new ArrayList();

		consume(TokenType.LBRACKET, "An dieser Stelle haben wir ein oeffnende Klammer: [ erwartet", err);
		while (!check(TokenType.RBRACKET)) {
			libs.add(consume(TokenType.STRINGLITERAL, "An dieser Stelle haben wir die zu importierende Datei oder Bibilothek erwartet", err));
			consume(TokenType.COMMA, "An dieser Stelle haben wir ein Komma erwartet", err);
		}
		consume(TokenType.RBRACKET, "An dieser Stelle haben wir eine schliessende Klammer: ] erwartet", err);
		return new Stmt.Import(libs);
    }

    public Expr.StructLiteral parseStructLiteral() {

		var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen eines StruktLiterals")
			.errMsg("StruktLiterale erlauben es dir Objekte mit werten und Leben zu befuellen!")
			.example("""
					 person1 := Person {
						 name: "Torben Groetzinger",
						 alter: 18,
						 hat_fuehrerschein: wahr,
					 };
					 """)
			.url("TODO.de")
			.build();

		var structName = consume(TokenType.IDEN, "An diser Stelle haben wir den Namen des zu erstellden Typen erwartet", err);
		consume(TokenType.STARTBLOCK, "An dieser Stelle haben wir eine oeffnende Klammer: { erwartet", err);

		var fields = new HashMap();
		while (!check(TokenType.ENDBLOCK)) {
			var fieldName = consume(TokenType.IDEN, "An dieser Stelle haben wir den Namen des Feldes dass du beschreiben moechtest erwartet" ,err);
			consume(TokenType.COLON, "An dieser Stelle haben wir ein : erwartet", err);
			var value = parseExpr();
			fields.put(fieldName, value);
			consume(TokenType.COMMA, "An dieser Stelle haben wir ein Komma erwartet", err);
		}
		consume(TokenType.ENDBLOCK, "An dieser Stelle haben wir ein schliessende Klammer } erwartet", err);
		return new Expr.StructLiteral(new TypeInfo(structName.getLexeme(), structName), fields);
    }


    //TODO(Simon): Finish parsing if Stmts
    public Stmt.If parseIf() {

		var err = Report.builder()
            .wasFatal(true)
            .errType("Fehle beim parsen einer Verzweigung")
            .errMsg("Mit dem wenn befehl kannst du entscheiden ob und wann eine bestimmte Stelle in deinem Programm ausgefuehrt wird!")
            .example("wenn foo > 0 dann {}")
			.example("wenn 2 > 0 dann {}")
			.url("TODO.de")
			.build();

		consume(TokenType.IF, err);
		Expr condition = parseExpr();
		consume(TokenType.THEN, "einem wenn muss auch ein dann folgen :D", err);
		var body = parseBlock();

		var primaryBranch = new Stmt.If.Branch(condition, body);

		var elseBranches = new ArrayList();

		while (check(TokenType.ELSE) && checkNext(TokenType.IF)) {
			consume(TokenType.ELSE, err);
			consume(TokenType.IF, err);
			var elseCondition = parseExpr();
			consume(TokenType.THEN, err);
			var elseBody = parseBlock();
			elseBranches.add(new Stmt.If.Branch(elseCondition, elseBody));
		}

		var finalBranchBlock = matchAny(TokenType.ELSE) ? parseBlock() : null;
		return new Stmt.If(primaryBranch, elseBranches, new Stmt.If.Branch(null, finalBranchBlock));
    }

    public Stmt.StructDecl parseStructDecl() {

		var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen eines DatenTypes!")
            .errMsg("Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient meinander abzuspichern")
			.example("""
					 Typ: Person {
						name: Text,
						alter: Zahl,
					 }
					 """
					 )
			.url("TODO.de")
			.build();

		consume(TokenType.CLASS, "An dieser Stelle haben wir das Typ schluesselwort erwartet", err);
		consume(TokenType.COLON, err);
		var structName = consume(TokenType.IDEN, err);

		consume(TokenType.STARTBLOCK, "An dieser Stelle haben wir eine oeffnende Klammer: { erwartet", err);

		var arguments = new ArrayList();
		while (!check(TokenType.ENDBLOCK)) {

			Token varName = consume(TokenType.IDEN, err);
			consume(TokenType.COLON, err);

			var type = parseTypeSpecifier();
			consume(TokenType.COMMA, err);

			arguments.add(new Stmt.StructDecl.Member(varName, type));
		}
		consume(TokenType.ENDBLOCK, err);

		// TODO(Simon): After we parse an impl block should we associate the methods
		// wh the right class?
		// Maybe do in seperate pass?
		return new Stmt.StructDecl(structName, arguments, null);
    }

    // TODO(Simon): add desugared increment in the body
    // TODO(Simon): check if range for the loop is valid
    public Stmt.While parseForLoop() {

		var err = Report.builder()
			.wasFatal(true)
			.errType("Fehler beim parsen einer fuer schleife")
			.errMsg("Schleifen erlauben es dir Code mehrmals auszufuerhren")
			.example(String.format("%s", "variablen_name := Wert"))
			.example(String.format("%s", "test := (4 + 2)"))
			.url("TODO.de")
			.build();
	
		consume(TokenType.FOR, err);
	
		/* TODO(Simon): Does not work with no loop variable like:

		   for 0 bis 10 {
	       foo();
		   }
	   
		   For this we need to insert an invisible variable into the body of the loop to keep track of the current position.
		*/
		var loopVar = consume(TokenType.IDEN, err);
		consume(TokenType.VARDEF, err);

		var start = parseExpr();
		consume(TokenType.UNTIL, err);
		var end = parseExpr();

		var condion = new Expr.Binary(start, new Token(TokenType.LESS, ">"), end);
		var incrementExpr = new Expr.Binary(new Expr.Literal(loopVar.getLexeme()), new Token(TokenType.PLUS, "+"), new Expr.Literal(1));
		var incrementAssingment = new Expr.Assign(loopVar, incrementExpr);

		var body = parseBlock();
		body.getStatements().add(new Stmt.Expression(incrementAssingment));
		return new Stmt.While(condion, body);
    }

    public Stmt.VarDef parseVarDef() {

		var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen einer Variablen Defintion")
            .errMsg("Mithilfe von Variablen kannst du Daten im Laufe deines Programmes speichern um diese an einem spaetern Zepunkt wieder zu verwenden")
            .example("variablen_name := Wert")
            .example("test := (4 + 2)")
            .url("TODO.de")
            .build();

		Token varName = consume(TokenType.IDEN, err);

		TypeInfo type = null;
		if (check(TokenType.VARDEF)) {
			consume(TokenType.VARDEF, err);
		} else if (check(TokenType.COLON)) {
			consume(TokenType.COLON, err); // user provided type information, variable is typed
			type = parseTypeSpecifier();
			consume(TokenType.EQUALSIGN, err);
		} else {
			consume(TokenType.VARDEF, err); // FIXME(Simon): This is only a short fix to provide a error message
		}

		Expr value = parseExpr();
		consume(TokenType.SEMICOLON, "Nach einer Variablendefintion haben wir ein Semicolon erwartet!", err);
		return new Stmt.VarDef(varName, type, value);
    }


    public Expr parseExpr() {
		return parseAssignment();
    }

    public Expr parseAssignment() {
		var ASTNode = parseOr();

		if (matchAny(TokenType.EQUALSIGN)) {
			var equals = previous();
			var value = parseAssignment();

			if (ASTNode instanceof Expr.Variable) {
				var name = ((Expr.Variable)ASTNode).getName();
				return new Expr.Assign(name, value); 
			} else if (ASTNode instanceof Expr.Get) {
				var getNode = (Expr.Get)ASTNode;
				return new Expr.Set(getNode.getObject(), getNode.getName(), value);
			}
			System.out.printf("%s %s", Chalk.on("[DEBUG ERROR]"), "Invalid Assignment target!!");
		}
		return ASTNode;
    }


    private Expr parseOr() {
		var ASTNode = parseAnd();

		while (matchAny(TokenType.OR)) {
			var operator = previous();
			var right = parseAnd();
			ASTNode = new Expr.Binary(ASTNode, operator, right);
		}
		return ASTNode;
    }

    private Expr parseAnd() {
		var ASTNode = parseEquality();

		while (matchAny(TokenType.AND)) {
			var operator = previous();
			var right = parseEquality();
			ASTNode = new Expr.Binary(ASTNode, operator, right);
		}
		return ASTNode;
    }

    private Expr parseEquality() {
		var ASTNode = parseComparison();

		while (matchAny(TokenType.EQUALEQUAL, TokenType.NOTEQUAL)) {
			var operator = previous();
			var right = parseComparison();
			ASTNode = new Expr.Binary(ASTNode, operator, right);
		}
		return ASTNode;
    }


    private Expr parseComparison() {
		var ASTNode = parseAddition();

		while (matchAny(TokenType.GREATER, TokenType.GREATEREQUAL, TokenType.LESS, TokenType.LESSEQUAL)) {
			var operator = previous();
			var right = parseAddition();
			ASTNode = new Expr.Binary(ASTNode, operator, right);
		}
		return ASTNode;
    }

    private Expr parseAddition() {
		var ASTNode = parseMultiplication();

		while (matchAny(TokenType.MINUS, TokenType.PLUS)) {
			var operator = previous();
			var right = parseMultiplication();
			ASTNode = new Expr.Binary(ASTNode, operator, right);
		}
		return ASTNode;
    }

    private Expr parseMultiplication() {
		var ASTNode = parseUnary();
		while (matchAny(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
			var operator = previous();
			var right = parseUnary();
			ASTNode = new Expr.Binary(ASTNode, operator, right);
		}
		return ASTNode;
    }

    private Expr parseUnary() {
		if (matchAny(TokenType.NOT, TokenType.MINUS)) {
			var operator = previous();
			var right = parseUnary();
			return new Expr.Unary(operator, right);
		}
		return parseFunctionCall();
    }

    private Expr parseFunctionCall() {
		var ASTNode = parsePrimary();

		while (true) {
			if (matchAny(TokenType.LPAREN)) {
				ASTNode = finishParsingCall(ASTNode);
			} else if (matchAny(TokenType.DOT)) {
				var err = Report.builder()
					.wasFatal(true)
					.errType("Unerwarteter Token!")
					.errMsg("Nach einem Punkt haben wir den namen des Feldes eines DatenTypen oder einer Methode erwartet!")
					.example("foo.bar")
					.example("person.name")
					.example("konto.guthaben")
					.example("foo.bar()")
					.url("TODO")
					.build();
				var name = consume(TokenType.IDEN, err);
				ASTNode = new Expr.Get(ASTNode, name);
			} else {
				break;
			}
		}
		return ASTNode;
    }

    private Expr finishParsingCall(Expr callee) {

		var err = Report.builder()
			.wasFatal(true)
			.errType("Unerwarteter Token!")
			.errMsg("Du scheinst eine schliessende Klammer in folgendem Funktionsaufruf vergessen zu haben!")
			.example("foo(42)")
			.example("bar(23, 23)")
			.example("""
					 fooBar("Hallo", "Welt");
					 """)
		.url("TODO")
		.build();
	var args = new ArrayList();
	while (!check(TokenType.RPAREN)) {
		args.add(parseExpr());
		matchAny(TokenType.COMMA);
	}

		var paren = consume(TokenType.RPAREN, err);
		return new Expr.Call(callee, paren, args);
    }

    private Expr.ArrayAccess parseArrayAccess() {
		var err = Report.builder()
			.wasFatal(true)
			.errType("Unerwarteter Token!")
			.errMsg("An dieser Stelle haben wir einen Feldzugriff erwartet")
			.example("a[0]")
			.example("foo[d +2]")
			.example("a[2][2]")
			.url("TODO")
			.build();
		var arrayName = consume(TokenType.IDEN, err);
		consume(TokenType.LBRACKET, err);
	    var index = parseExpr();
	    consume(TokenType.RBRACKET, err);
	    return new Expr.ArrayAccess(arrayName, index);
	}

	private Expr parsePrimary() {

	    if (matchAny(TokenType.FALSE)) return new Expr.Literal(false);
	    if (matchAny(TokenType.TRUE)) return new Expr.Literal(true);
	    if (matchAny(TokenType.NULL)) return new Expr.Literal(null);
	    if (matchAny(TokenType.STRINGLITERAL, TokenType.NUMBERLITERAL)) {
			return new Expr.Literal(previous().getLiteral());  
	    } 
	    if (matchAny(TokenType.SELF)) return new Expr.Self(previous());

	    if (check(TokenType.IDEN)) {
			if (checkNext(TokenType.STARTBLOCK)) {
				return parseStructLiteral();
			} else if (checkNext(TokenType.LBRACKET)) {
				return parseArrayAccess();
			}
			return new Expr.Variable(next());   
	    }

	    if (check(TokenType.READINPUT)) return parseInput();

	    if (matchAny(TokenType.LPAREN)) {
			var ASTNode = parseExpr();
			var err = Report.builder()
				.wasFatal(true)
				.errType("Unerwarteter Token!")
				.errMsg("Du scheinst eine schliessende Klammer in folgendem Mathematischem Ausdruck vergessen zu haben!")
				.example("(a + 3)")
				.example("((42 - 3) - 2)")
				.example("(foo(2) -2)")
				.url("TODO")
				.build();
			consume(TokenType.RPAREN, err);
			return new Expr.Grouping(ASTNode);
	    }
	    Report.builder()
			.wasFatal(true)
			.errType("Unerwarteter Token!")
			.errMsg("An dieser Stelle haben wir einen Mathematischen ausdruck erwartet!")
			.example("(a + 3)")
			.example("((42 - 3) - 2)")
			.example("(foo(2) -2)")
			.token(peek())
			.url("TODO")
			.build()
			.print();
	    System.out.println(Chalk.on("[Debug]").red().bold() + ":: This should be unreachable because of the exception thrown in the line before. Internal compiler error!");
	    return null;
	}
    
    public Stmt.Expression parseExprStmt() {
		var err = Report.builder()
			.wasFatal(true)
			.errType("Semicolon vergessen")
			.errMsg("Du scheinst ein Semicolon nach einem Ausdruck vergessen zu haben!")
			.example("test = 3;")
			.example("foo();")
			.example("foo().test = 23;")
			.url("TODO.de")
			.build();
		var ASTNode = parseExpr();
		consume(TokenType.SEMICOLON, err);
		return new Stmt.Expression(ASTNode);
    }

    private Token consume(TokenType type, Report err) {
		if (check(type)) return next();   
		err.setToken(peek());
		err.print();
		System.out.println(Chalk.on("[Debug]").green().bold() + ":: This should be unreachable because of the exception thrown in the line before. Internal compiler error!");
		return null; // unreachable code becase sync will throw an execption
    }

    private Token consume(TokenType type, String errMsg, Report err) {
		if (check(type)) return next();
		err.setToken(peek());
	    err.setErrMsg(errMsg);
		err.print();
		System.out.println(Chalk.on("[Debug]").green().bold() + ":: This should be unreachable because of the exception thrown in the line before. Internal compiler error!");
		return null; // unreachable code becase sync will throw an execption
    }

    public boolean matchAny(TokenType... types) {
		if (Arrays.stream(types).anyMatch(type -> check(type))) {
			next();
			return true;
		}
		return false;
    }
    public boolean checkNext(TokenType type) {
		return peekNext().getType() == type;
    }

    public boolean check(TokenType... types) {
		if (!hasNext()) return false;
		return Arrays.stream(types).anyMatch(type -> type == peek().getType());
    }
}

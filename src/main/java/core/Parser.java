package core;

import java.util.*;
import util.*;

// TODO(Simon): add parsing for function calls
// TODO(Simon): finish parsing blocks

// TODO(Simon): make err messages better and more descriptive. Maybe we should handle more cases with their own err msg
public class Parser extends Iter<Token> {

    public Parser(Iter<Token> it) {
	super(it.getBuffer()); // TODO(Simon): write better consturctor
    }

    public void sync() {
	// consume tokens until we reach the next stmt to continue parsing
	while (hasNext()) {
	    switch (peek().getType()) {
	    case CLASS, FUNCTION, VARDEF:
		return;
	    }
	    next();
	}
    }
    public List<Stmt> parse() {

	var ast = new ArrayList();
	if (check(TokenType.IMPORT)) {
		var libs = parseImport();
		ast.add(libs); // NOTE(Simon): we have to resolve this later to parse all sourcefiles, should be fairly easy to thread this
	}
	while (this.hasNext())	 {
	    var stmt = parseStmt();
	    ast.add(stmt);
	} 
	return ast;
    }
    public Stmt parseStmt() {
	try {
	    while (hasNext() && !check(TokenType.EOF)) {
		return switch (peek().getType()) {
		case CLASS: yield parseStructDecl();
		case FUNCTION: yield parseFunctionDecl();
		case IDEN: yield parseVarDef();
		default: {
		    var err = Report.builder()
                        .wasFatal(true)
			.errType("Token nicht erkannt")
                        .errMsg("lol")
                        .token(next())
                        .example(String.format("%s", "Typ: Haus {}"))
                        .example(String.format(
					       "%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
                        .url("TODO.de")
                        .build();
		    System.out.println(err);
		    err.sync();
		    yield null; // unreachable because we throw an exception
		}
		};
	    
	    }
	} catch(Report.Error e) {
	    sync();
	    System.out.println("FAILED NEED TO SYNC PARSER");
	}
	return null;
    }

    public Expr parseExpr() { return equaly(); }

    private Expr equaly() {
	Expr expr = comparison();

	while (matchAny(TokenType.NOTEQUAL, TokenType.EQUALEQUAL)) {
	    Token operator = previous();
	    Expr right = comparison();
	    expr = new Expr.Binary(expr, operator, right);
	}
	return expr;
    }

    private Expr comparison() {
	Expr expr = addtion();

	while (matchAny(TokenType.GREATER, TokenType.GREATEREQUAL, TokenType.LESS,
			TokenType.LESSEQUAL)) {
	    Token operator = previous();
	    Expr right = addtion();
	    expr = new Expr.Binary(expr, operator, right);
	}
	return expr;
    }

    private Expr addtion() {
	Expr expr = multiplication();

	while (matchAny(TokenType.PLUS, TokenType.MINUS)) {
	    Token operator = previous();
	    Expr right = multiplication();
	    expr = new Expr.Binary(expr, operator, right);
	}
	return expr;
    }

    private Expr multiplication() {
	Expr expr = unary();

	while (matchAny(TokenType.MULTIPLY, TokenType.DIVIDE)) {
	    Token operator = previous();
	    Expr right = unary();
	    expr = new Expr.Binary(expr, operator, right);
	}
	return expr;
    }

    private Expr unary() {
	if (matchAny(TokenType.NOT, TokenType.MINUS)) {
	    Token operator = previous();
	    Expr right = unary();
	    return new Expr.Unary(operator, right);
	}
	return primary();
    }

    private Expr primary() {
	if (matchAny(TokenType.NULL))
	    return new Expr.Literal(null);
	if (matchAny(TokenType.FALSE))
	    return new Expr.Literal(false);
	if (matchAny(TokenType.TRUE))
	    return new Expr.Literal(true);

	if (matchAny(TokenType.STRINGLITERAL, TokenType.NUMBERLITERAL)) {
	    return new Expr.Literal(previous().getLiteral());
	}

	if (matchAny(TokenType.LPAREN)) {

	    var err = Report.builder()
		.wasFatal(true)
		.errType("Mathematischer Ausdruck nicht geschlossen")
		.errMsg("Hey wir waren gerade dabei einen Mathematischen Ausdruck zu parsen, es scheint als haettest du vergessen eine Klammer zu schliesen")
		.url("www.TODO.de")
		.build();

	    Expr expr = parseExpr();
	    consume(TokenType.RPAREN, err);
	    return new Expr.Grouping(expr);
	}
	// TODO(Simon): We should provide a better error for the user if the parens
	// are not balanced
	return null; // should never be reached
    }

    // TODO(Simon): use our report system for error handling and warning
    public Stmt.FunctionDecl parseFunctionDecl() {


	var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen einer Funktion")
            .errMsg("Wir waren gerade dabei eine funktion zu parsen als wir einen Fehler gefunden haben. Nach dem Fun Schluesselwort haben wir den namen der funktion erwartet!")
	    .url("TODO") 
	    .build();
	consume(TokenType.FUNCTION, err, "fun erwartet");

	Token functionName = consume(TokenType.IDEN, err);
	consume(TokenType.LPAREN, err);

	var params = new ArrayList();

	while (!check(TokenType.RPAREN)) {
	    Token paramName = consume(TokenType.IDEN, err);
	    consume(TokenType.COLON, err);
	    Token paramType = consume(TokenType.IDEN, err);
	    params.add(new Stmt.FunctionDecl.Parameter(paramName, paramType));
	    if (check(TokenType.RPAREN)) break;
	    consume(TokenType.COMMA, err);
	}
	consume(TokenType.RPAREN, err);

	Token returnType = null;
	// TODO(Simon): we could allow tuple return types in a later edion of the language
	if (!check(TokenType.STARTBLOCK)) {
	    consume(TokenType.ARROW, err);
	    returnType = consume(TokenType.IDEN, err);
	}

	var body = parseBlock();
	return new Stmt.FunctionDecl(functionName, params, returnType, body);
    }

    public Stmt.Block parseBlock() {

	var err = Report.builder()
	    .wasFatal(true)
	    .errType("Block nicht begrenzt")
	    .errMsg("Fehler beim parsen eines Blocks")
	    .url("www.TODO.de")
	    .build();

	consume(TokenType.STARTBLOCK, err);

	var stmts = new ArrayList();

	while (!check(TokenType.ENDBLOCK)) {
	    var stmt = switch (peek().getType()) {
	    case IDEN: {
		if (checkNext(TokenType.VARDEF)) yield parseVarDef();
		if (checkNext(TokenType.COLON)) yield parseVarDef();
		else if (checkNext(TokenType.EQUALSIGN)) yield parseAssignment();
	    }
	    case IF: yield parseIf();
	    case FOR: yield parseForLoop();
	    case WHILE: yield parseWhileLoop();
	    case PRINT: yield parsePrint();
	    case RETURN: yield parseReturn();
	    case BREAK: yield parseBreak();
	    default: yield null;
	    };
	    if (stmt != null) stmts.add(stmt);
	}

	consume(TokenType.ENDBLOCK, err);
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
	consume(TokenType.SEMICOLON, err, "Semicolon erwartet");
	return new Stmt.Break(location);
    }

    public Expr.Input parseInput() {

	var err = Report.builder()
	    .wasFatal(true)
	    .errType("Fehler beim parsen eines Input stmts")
	    .errMsg("Hey wir waren gerade dabei einen Mathematischen Ausdruck zu parsen, es scheint als haettest du vergessen eine Klammer zu schliesen")
	    .url("www.TODO.de")
	    .build();

	consume(TokenType.READINPUT, err);
	consume(TokenType.LPAREN, err);
	var msg = consume(TokenType.STRINGLITERAL, err);
	consume(TokenType.RPAREN, err);
	return new Expr.Input(msg);
    }

    public Stmt.Assignment parseAssignment() {

	var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen einer Zuweisung")
            .errMsg("Der = operator erlaubt es dir den in einer variablen gespeicherten Wert zu aendern!")
            .url("www.TODO.de")
            .build();
	var varName = consume(TokenType.IDEN, err);
	consume(TokenType.EQUALSIGN, err);
	var expr = parseExpr();
	consume(TokenType.SEMICOLON, err, "Semicolon erwartet");
	return new Stmt.Assignment(varName, expr);
    }

    public Stmt.Print parsePrint() {

	var err = Report.builder()
            .wasFatal(true)
            .errType("Mathematischer Ausdruck nicht geschlossen")
            .errMsg("Hey wir waren gerade dabei einen Mathematischen Ausdruck zu parsen, es scheint als haettest du vergessen eine Klammer zu schliesen")
            .url("www.TODO.de")
            .build();

	consume(TokenType.PRINT, err);
	consume(TokenType.LPAREN, err);

	var formatter = consume(TokenType.STRINGLITERAL,
				err); // TODO(Simon): we should really provide a
	// good error if the formatter is missing

	var exprs = new ArrayList();
	while (!check(TokenType.RPAREN)) {

	    consume(TokenType.COMMA, err);

	    // TODO(Simon): add suport for printing structs
	    exprs.add(parseExpr());
	}
	consume(TokenType.RPAREN, err);
	consume(TokenType.SEMICOLON, err, "Nach einem ausgabe ausdruck haben wir ein Semicolon erwartet!");

	return new Stmt.Print(formatter, exprs);
    }

    public Stmt.While parseWhileLoop() {

	var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen einer Solange schleife")
            .errMsg("Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient meinander abzuspichern")
            .example(String.format("%s", "Typ: Haus {}"))
            .example(String.format("%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
            .url("TODO.de")
            .build();
	consume(TokenType.WHILE, err);

	var condion = parseExpr();
	var body = parseBlock();

	return new Stmt.While(condion, body);
    }

    public Stmt.Return parseReturn() {

	var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen eines rueckgabe Befehls")
            .errMsg("Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient meinander abzuspichern")
            .example(String.format("%s", "Typ: Haus {}"))
            .example(String.format(
				   "%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
            .url("TODO.de")
            .build();

	var location = consume(TokenType.RETURN, err);
	Expr expr = parseExpr();
	consume(TokenType.SEMICOLON, err, "Semicolon nach Mathematischem Ausdruck erwartet");
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
            .errType("Wir sind uns nicht ganz sicher welche Bibolotheken du meinst!")
            .errMsg("M dem '#benutze' Befehl kannst du andere Datein und externe Bibilotheken in deinem Programm benuzten.")
            .example(String.format("%s", "#benutze ['Mathe', 'Eingabe']"))
            .url("TODO.de")
            .build();
	consume(TokenType.IMPORT, err);

	var libs = new ArrayList();

	consume(TokenType.LBRACKET, err);
	while (!check(TokenType.RBRACKET)) {
	    libs.add(consume(TokenType.STRINGLITERAL, err));
	    consume(TokenType.COMMA, err);
	}
	consume(TokenType.RBRACKET, err);
	return new Stmt.Import(libs);
    }

    public Stmt.If parseIf() {

	var err = Report.builder()
            .wasFatal(true)
            .errType("Fehle beim parsen einer Verzweigung")
            .errMsg("Mit dem wenn befehl kannst du entscheiden ob und wann eine bestimmte Stelle in deinem Programm ausgefuehrt wird!")
            .example(String.format("%s", "wenn foo > 0 {}"))
            .example(String.format("%s", "wenn 2 > 0 {}"))
            .url("TODO.de")
            .build();

	consume(TokenType.IF, err);
	Expr condion = parseExpr();

	var body = parseBlock();
	
	Stmt elseIFBranch = null;
	Stmt elseBranch = null;

	return new Stmt.If(condion, body, null, null);
    }

    public Stmt.Class parseStructDecl() {

	// NOTE(Simon): we arrive here after the Typ Keyword
	next();

	var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen eines DatenTypes!")
            .errMsg("Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient meinander abzuspichern")
            .example(String.format("%s", "Typ: Haus {}"))
            .example(String.format("%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
            .url("TODO.de")
            .build();

	consume(TokenType.COLON, err);
	var structName = consume(TokenType.IDEN, err);

	consume(TokenType.STARTBLOCK, err);

	var arguments = new ArrayList();
	while (!check(TokenType.ENDBLOCK)) {

	    Token varName = consume(TokenType.IDEN, err);
	    consume(TokenType.COLON, err);

	    Token typeName = consume(TokenType.IDEN, err);
	    consume(TokenType.COMMA, err);

	    arguments.add(new Stmt.Class.Member(varName, typeName));
	}
	consume(TokenType.ENDBLOCK, err);

	// TODO(Simon): After we parse an impl block should we associate the methods
	// wh the right class?
	// Maybe do in seperate pass?
	return new Stmt.Class(structName, arguments, null);
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

	var condion = new Expr.Binary(start, new Token(TokenType.LESS), end);

	var incrementExpr = new Expr.Binary(new Expr.Literal(loopVar.getLexeme()), new Token(TokenType.PLUS), new Expr.Literal(1));
	var incrementAssingment = new Stmt.Assignment(loopVar, incrementExpr);

	var body = parseBlock();
	body.getStatements().add(incrementAssingment);
	return new Stmt.While(condion, body);
    }

    public Stmt.VarDef parseVarDef() {

	var err = Report.builder()
            .wasFatal(true)
            .errType("Fehler beim parsen einer Variablen Defintion")
            .errMsg("Mithilfe von Variablen kannst du Daten im Laufe deines Programmes speichern um diese an einem spaetern Zepunkt wieder zu verwenden")
            .example(String.format("%s", "variablen_name := Wert"))
            .example(String.format("%s", "test := (4 + 2)"))
            .url("TODO.de")
            .build();

	// we assume that we peeked ahead to find the :=  Symbol
	Token varName = consume(TokenType.IDEN, err);

	Token typeName = null;
	if (check(TokenType.VARDEF)) {
	    consume(TokenType.VARDEF, err);
	} else if (check(TokenType.COLON)) {
	    // user provided type information, variable is typed
	consume(TokenType.COLON, err);
	typeName = consume(TokenType.IDEN, err);
	consume(TokenType.EQUALSIGN, err);
	}

	Expr value = parseExpr();
	consume(TokenType.SEMICOLON, err);
	return new Stmt.VarDef(varName, typeName, value);
    }

    private Token consume(TokenType type, Report err) {
	if (check(type))
	    return next();
	err.setToken(next());
	System.out.println(err);
	err.sync();
	return null; // unreachable code becase sync will throw an execption
    }

    private Token consume(TokenType type, Report err, String errMsg) {
	if (check(type))
	    return next();
	err.setToken(next());
	err.setErrMsg(errMsg);
	System.out.println(err);
	err.sync();
	return null; // unreachable code becase sync will throw an execption
    }

    public boolean matchAny(TokenType... types) {
	for (TokenType type : types) {
	    if (check(type)) {
		next();
		return true;
	    }
	}
	return false;
    }
    public boolean checkNext(TokenType type) {
	return peekNext().getType() == type;
    }

    public boolean check(TokenType type) {
	if (!hasNext()) return false;
	return peek().getType() == type;
    }
}

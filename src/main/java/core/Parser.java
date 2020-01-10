package core;

import util.*;
import java.util.*;

// TODO(Simon): make err messages better and more descriptive. Maybe we should handle more cases with their own err msg

public class Parser {

    public static void sync(Iter<Token> it) {

	it.next();


	while (it.hasNext()) {
	    // consume tokens until we reach the next stmt to continue parsing
	    switch (it.peek().getType()) {
	    case CLASS, FUNCTION, VARDEF: return;
	    }
	    it.next();
	}
    }

    public static List<Stmt> parse(Iter<Token> it) {

	var ast = new ArrayList();

	var libs = parseImport(it);
	ast.add(libs); // NOTE(Simon): we have to resolve this later to parse all sourcefiles, it should be fairly easy to thread this

	while (it.hasNext() && !check(TokenType.EOF, it)) {
	    var stmt = switch (it.peek().getType()) {
	    case CLASS: yield parseStruct(it);
	    case FUNCTION: yield parseFunctionDecl(it);
	    case SYMBOL: {
		if (check(TokenType.COLON, it) || check(TokenType.VARDEF, it)) {
		    yield parseVarDef(it);
		}
	    };

	    default: {
		var err = new Report.Builder()
		    .errWasFatal()
		    .setErrorType("Token nicht erkannt")
		    .withErrorMsg("lol")
		    .atToken(it.next())
		    .addExample(String.format("%s", "Typ: Haus {}"))
		    .addExample(String.format("%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
		    .url("TODO.de")
		    .create();
		System.out.println(err);
		err.sync();
		yield null; // this is unreachable because we throw an execption beforehand
	    }
	    };
	    ast.add(stmt);
	}
	return ast;
    }

    public static Expr parseExpr(Iter<Token> it) {
	return equality(it);
    }

    private static Expr equality(Iter<Token> it) {
	Expr expr = comparison(it);

	while (matchAny(it, TokenType.NOTEQUAL, TokenType.EQUALEQUAL)) {
	    Token operator = it.previous();
	    Expr right = comparison(it);
	    expr = new Expr.Binary(expr, operator, right);
	}
	return expr;
    }

    private static Expr comparison(Iter<Token> it) {
	Expr expr = addtion(it);

	while (matchAny(it, TokenType.GREATER, TokenType.GREATEREQUAL, TokenType.LESS, TokenType.LESSEQUAL))  {
	    Token operator = it.previous();
	    Expr right = addtion(it);
	    expr = new Expr.Binary(expr, operator, right);
	}
	return expr;
    }

    private static Expr addtion(Iter<Token> it) {
	Expr expr = multiplication(it);

	while (matchAny(it, TokenType.PLUS, TokenType.MINUS)) {
	    Token operator = it.previous();
	    Expr right = multiplication(it);
	    expr = new Expr.Binary(expr, operator, right);
	}
	return expr;
    }

    private static Expr multiplication(Iter<Token> it) {
	Expr expr = unary(it);

	while (matchAny(it, TokenType.MULTIPLY, TokenType.DIVIDE)) {
	    Token operator = it.previous();
	    Expr right = unary(it);
	    expr = new Expr.Binary(expr, operator, right);
	}
	return expr;
    }

   private static Expr unary(Iter<Token> it) {
       if (matchAny(it, TokenType.NOT, TokenType.MINUS)) {
	   Token operator = it.previous();
	   Expr right = unary(it);
	   return new Expr.Unary(operator, right);
       }
       return primary(it);
    }

    private static Expr primary(Iter<Token> it) {
	if (matchAny(it, TokenType.NULL)) return new Expr.Literal(null);
	if (matchAny(it, TokenType.FALSE)) return new Expr.Literal(false);
	if (matchAny(it, TokenType.TRUE)) return new Expr.Literal(true);

	if (matchAny(it, TokenType.STRINGLITERAL, TokenType.NUMBERLITERAL)) {
	    return new Expr.Literal(it.previous().getLiteral());
	}
	if (matchAny(it, TokenType.LPAREN)) {

	    var err = new Report.Builder()
		.errWasFatal()
		.setErrorType("Mathematischer Ausdruck nicht geschlossen")
		.withErrorMsg("Hey wir waren gerade dabei einen Mathematischen Ausdruck zu parsen, es scheint als haettest du vergessen eine Klammer zu schliesen")
		.url("www.TODO.de")
		.create();

		Expr expr = parseExpr(it);
		consume(TokenType.RPAREN, err, it);
		return new Expr.Grouping(expr);
	    }
	// TODO(Simon): We should provide a better error for the user if the parens are not balanced
	return null; // should never be reached
    }

    //TODO(Simon): use our report system for error handling and warning
    public static Stmt parseFunctionDecl(Iter<Token> it) {

	it.next();
	
	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Fehler beim parsen einer Funktion")
	    .withErrorMsg("Wir waren gerade dabei eine funktion zu parsen als wir einen Fehler gefunden haben. Nach dem Fun Schluesselwort haben wir den namen der funktion erwartet!")
	    .url("TODO") // TODO(Simon): add url and examples of correct function definition
	    .create();
	
	Token functionName = consume(TokenType.SYMBOL, err, it);
	consume(TokenType.LPAREN, err, it);

	var args = new ArrayList();

	while (!check(TokenType.RPAREN, it)) {
	    Token varName = consume(TokenType.SYMBOL, err, it);
	    consume(TokenType.COLON, err, it);
	    Token typeName = consume(TokenType.SYMBOL, err, it);
	    args.add(new Stmt.FunctionDecl.Parameter(varName, typeName));
	}
	consume(TokenType.RPAREN, err, it);

	Token returnType = null;
	// TODO(Simon): we could allow tuple return types in a later edition to the language
	if (!check(TokenType.RPAREN, it)) {
	    consume(TokenType.ARROW, err, it); 
	    returnType = consume(TokenType.SYMBOL, err, it);  
	}

	consume(TokenType.STARTBLOCK, err, it);
	var body = parseBlock(it);
	return new Stmt.FunctionDecl(functionName, args, returnType, body);
    }

    public static Stmt parseBlock(Iter<Token> it) {

	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Block nicht begrenzt")
	    .withErrorMsg("Fehler beim parsen eines Blocks")
	    .url("www.TODO.de")
	    .create();

	consume(TokenType.STARTBLOCK, err, it);

	var stms = new ArrayList();

	while (!check(TokenType.ENDBLOCK, it)) {
	    var stmt = switch (it.peek().getType()) {
	    case SYMBOL: yield parseVarDef(it);
	    case IF: yield parseIfStmt(it);
	    case FOR: yield parseForLoop(it);
	    case WHILE: yield parseWhileLoop(it);
	    case PRINT: yield parsePrint(it);
	    case READINPUT: yield parseInput(it);
	    case RETURN: yield parseReturn(it);
	    case BREAK: 
	    default: yield null;
	    };
	}
	
	consume(TokenType.ENDBLOCK, err, it);
	return null;
    }

    public static Stmt parsePrint(Iter<Token> it) {

	
	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Mathematischer Ausdruck nicht geschlossen")
	    .withErrorMsg("Hey wir waren gerade dabei einen Mathematischen Ausdruck zu parsen, es scheint als haettest du vergessen eine Klammer zu schliesen")
	    .url("www.TODO.de")
	    .create();
	
	consume(TokenType.PRINT, err, it);
	consuem(TokenType.LPAREN, err, it);
	var formatter = consume(TokenType.STRINGLITERAL, err, it); // TODO(Simon): we should really provide a good error if the formatter is missing


	var output = new ArrayList();
	while (!check(TokenType.RPAREN, it)) {

	    consume(TokenType.COMMA, err, it);

	    // TODO(Simon): add checking for string literals
	    output.add(consume(TokenType.SYMBOL, err, it));
	}
	consume(TokenType.RPAREN, err, it);
    }
    
    public static Stmt parseWhileLoop(Iter<Token> it) {
	return null;
    }

    public static Stmt parseReturn(Iter<Token> it) {

	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Fehler beim parsen eines DatenTypes!")
	    .withErrorMsg("Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient miteinander abzuspichern")
	    .addExample(String.format("%s", "Typ: Haus {}"))
	    .addExample(String.format("%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
	    .url("TODO.de")
	    .create();

	consume(TokenType.RETURN, err, it);
	
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
    public static Stmt parseImport(Iter<Token> it) {

	it.next();
	
	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Wir sind uns nicht ganz sicher welche Bibolotheken du meinst!")
	    .withErrorMsg("Mit dem '#benutze' Befehl kannst du andere Datein und externe Bibilotheken in deinem Programm benuzten.")
	    .addExample(String.format("%s", "#benutze ['Mathe', 'Eingabe']"))
	    .url("TODO.de")
	    .create();
	
	var libs = new ArrayList();
	
	consume(TokenType.LBRACKET, err, it);
	while (!check(TokenType.RBRACKET, it)) {
	    libs.add(consume(TokenType.STRINGLITERAL, err, it));
	    consume(TokenType.COMMA, err, it);
	    System.out.println("text");

	}
	consume(TokenType.RBRACKET, err, it);
	return new Stmt.Import(libs);
    }

    public static Stmt parseIfStmt(Iter<Token> it) {

	
	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Fehle beim parsen einer Verzweigung")
	    .withErrorMsg("Mit dem wenn befehl kannst du entscheiden ob und wann eine bestimmte Stelle in deinem Programm ausgefuehrt wird!")
	    .addExample(String.format("%s", "wenn foo > 0 {}"))
	    .addExample(String.format("%s", "wenn 2 > 0 {}"))
	    .url("TODO.de")
	    .create();
	
	consume(TokenType.IF, err, it);

	// NOTE("wenn" keyword is already identified)
	Expr condition = parseExpr(it);

	var block = parseBlock(it);
	return new Stmt.If(condition, null, null);
    }

    public static Stmt parseStruct(Iter<Token> it) {

	// NOTE(Simon): we arrive here after the Typ Keyword
	it.next();
	
	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Fehler beim parsen eines DatenTypes!")
	    .withErrorMsg("Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient miteinander abzuspichern")
	    .addExample(String.format("%s", "Typ: Haus {}"))
	    .addExample(String.format("%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
	    .url("TODO.de")
	    .create();

	consume(TokenType.COLON, err, it);
	var structName = consume(TokenType.SYMBOL, err, it);

	consume(TokenType.STARTBLOCK, err, it);

	var arguments = new ArrayList();
	while (!check(TokenType.ENDBLOCK, it)) {

	    Token varName = consume(TokenType.SYMBOL, err, it);
	    consume(TokenType.COLON, err, it);

	    Token typeName = consume(TokenType.SYMBOL, err, it);
	    consume(TokenType.COMMA, err, it);

	    arguments.add(new Stmt.Class.Attribute(varName, typeName));
	}
	consume(TokenType.ENDBLOCK, err, it);


	//TODO(Simon): After we parse an impl block should we associate the methods with the right class?
	// Maybe do in seperate pass?
	return new Stmt.Class(structName, null, arguments);
    }

    public static Stmt parseForLoop(Iter<Token> it) {

	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Fehler beim parsen einer fuer schleife")
	    .withErrorMsg("Schleifen erlauben es dir Code mehrmals auszufuerhren")
	    .addExample(String.format("%s", "variablen_name := Wert"))
	    .addExample(String.format("%s", "test := (4 + 2)"))
	    .url("TODO.de")
	    .create();

	consume(TokenType.FOR, err, it);
	var loopVar = consume(TokenType.SYMBOL, err, it);
	consume(TokenType.VARDEF, err, it);

	var start = consume(TokenType.NUMBERLITERAL, err, it);
	consume(TokenType.UNTIL, err, it);
	var end = consume(TokenType.NUMBERLITERAL, err, it);

	var left = new Expr.Literal(start);
	var right = new Expr.Literal(end);

	var operator = new Token(TokenType.LESSEQUAL);
	var condition = new Expr.Binary(left, operator, right);

	var body = parseBlock(it);
	
	return new Stmt.While(condition, body);
    }

    public static Stmt parseVarDef(Iter<Token> it) {


	var err = new Report.Builder()
	    .errWasFatal()
	    .setErrorType("Fehler beim parsen einer Variablen Defintion")
	    .withErrorMsg("Mithilfe von Variablen kannst du Daten im Laufe deines Programmes speichern um diese an einem spaetern Zeitpunkt wieder zu verwenden")
	    .addExample(String.format("%s", "variablen_name := Wert"))
	    .addExample(String.format("%s", "test := (4 + 2)"))
	    .url("TODO.de")
	    .create();
	
	// we assume that we peeked ahead to find the :=  Symbol
	Token typeName = null;
	Token varName = consume(TokenType.SYMBOL, err, it);

	if (it.peek().getType() != TokenType.VARDEF) {
	    consume(TokenType.VARDEF, err, it);
	} else if (it.peek().getType() == TokenType.COLON) {
	    // user provided type information, variable is typed
	    consume(TokenType.COLON, err, it);
	    typeName = consume(TokenType.SYMBOL, err, it);
	    consume(TokenType.EQUALSIGN, err, it);
	}

	Expr value = parseExpr(it);
	consume(TokenType.SEMICOLON, err, it);
	return new Stmt.VarDef(varName, typeName, value);
    }

    private static Token consume(TokenType type, Report err, Iter<Token> it) {
	if (check(type, it)) return it.next();
	err.setToken(it.next());
	System.out.println(err);
	err.sync();
	return null; // unreachable code becase sync will throw an execption
    }
    
    public static boolean matchAny(Iter<Token> it, TokenType... types) {
	for (TokenType type : types) {
	    if (check(type, it)) {
		it.next();
		return true;
	    }
	}
	return false;
    }

    public static boolean check(TokenType type, Iter<Token> it) {
	if (!it.hasNext()) return false;
	return it.peek().getType() == type;
    }
}

package core;

import java.util.*;
import util.*;

// TODO(Simon): add parsing for assingments
// TODO(Simon): add parsing for function calls
// TODO(Simon): finish parsing blocks

// TODO(Simon): make err messages better and more descriptive. Maybe we should handle more cases with their own err msg
public class Parser extends Iter<Token> {

    public Parser(Iter<Token> it) {
	super(it.getBuffer()); // TODO(Simon): write better consturctor
    }

    public void sync() {
	next();
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
	var libs = parseImport();
	ast.add(libs); // NOTE(Simon): we have to resolve this later to parse all sourcefiles, should be fairly easy to thread this
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
		case CLASS:
		    yield parseStructDecl();
		case FUNCTION:
		    yield parseFunctionDecl();
		case IDEN: {
		    yield switch (peek(2).getType()) { // peek 2 Tokens into the future
		    case COLON, VARDEF:
			yield parseVarDef();
		    case EQUALSIGN:
			yield parseAssignment();
		    default: yield null; // should be unrechable
		    };
		}
		default: {
		    var err = new Report.Builder()
                        .errWasFatal()
                        .setErrorType("Token nicht erkannt")
                        .withErrorMsg("lol")
                        .atToken(next())
                        .addExample(String.format("%s", "Typ: Haus {}"))
                        .addExample(String.format(
						  "%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
                        .url("TODO.de")
                        .create();
		    System.out.println(err);
		    err.sync();
		    yield null; // unreachable because we throw an exception
		}
		};
	    }
	} catch(Report.ParseError e) {
	    sync();
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

	    var err =
		new Report.Builder()
		.errWasFatal()
		.setErrorType("Mathematischer Ausdruck nicht geschlossen")
		.withErrorMsg(
			      "Hey wir waren gerade dabei einen Mathematischen Ausdruck zu parsen, es scheint als haettest du vergessen eine Klammer zu schliesen")
		.url("www.TODO.de")
		.create();

	    Expr expr = parseExpr();
	    consume(TokenType.RPAREN, err);
	    return new Expr.Grouping(expr);
	}
	// TODO(Simon): We should provide a better error for the user if the parens
	// are not balanced
	return null; // should never be reached
    }

    // TODO(Simon): use our report system for error handling and warning
    public Stmt parseFunctionDecl() {

	next();

    var err =
        new Report.Builder()
            .errWasFatal()
            .setErrorType("Fehler beim parsen einer Funktion")
            .withErrorMsg("Wir waren gerade dabei eine funktion zu parsen als wir einen Fehler gefunden haben. Nach dem Fun Schluesselwort haben wir den namen der funktion erwartet!")
	.url("TODO") // TODO(Simon): add url and examples of correct
	// function definion
	.create();

    Token functionName = consume(TokenType.IDEN, err);
    consume(TokenType.LPAREN, err);

    var args = new ArrayList();

    while (!check(TokenType.RPAREN)) {
	Token varName = consume(TokenType.IDEN, err);
	consume(TokenType.COLON, err);
	Token typeName = consume(TokenType.IDEN, err);
	args.add(new Stmt.FunctionDecl.Parameter(varName, typeName));
    }
    consume(TokenType.RPAREN, err);

    Token returnType = null;
    // TODO(Simon): we could allow tuple return types in a later edion to the
    // language
    if (!check(TokenType.RPAREN)) {
      consume(TokenType.ARROW, err);
      returnType = consume(TokenType.IDEN, err);
    }

    consume(TokenType.STARTBLOCK, err);
    var body = parseBlock();
    return new Stmt.FunctionDecl(functionName, args, returnType, body);
  }

  public Stmt parseBlock() {

    var err = new Report.Builder()
                  .errWasFatal()
                  .setErrorType("Block nicht begrenzt")
                  .withErrorMsg("Fehler beim parsen eines Blocks")
                  .url("www.TODO.de")
                  .create();

    consume(TokenType.STARTBLOCK, err);

    var stms = new ArrayList();

    while (!check(TokenType.ENDBLOCK)) {
      var stmt = switch (peek().getType()) {
      case IDEN:
        yield parseVarDef();
      case IF:
        yield parseIfStmt();
      case FOR:
        yield parseForLoop();
      case WHILE:
        yield parseWhileLoop();
      case PRINT:
        yield parsePrint();
      case READINPUT:
        yield parseInput();
      case RETURN:
        yield parseReturn();
      case BREAK:
      default:
        yield null;
      };
    }

    consume(TokenType.ENDBLOCK, err);
    return null;
  }

  public Stmt parseInput() {

    var err =
        new Report.Builder()
            .errWasFatal()
            .setErrorType("Fehler beim parsen eines Input stmts")
            .withErrorMsg(
                "Hey wir waren gerade dabei einen Mathematischen Ausdruck zu parsen, es scheint als haettest du vergessen eine Klammer zu schliesen")
            .url("www.TODO.de")
            .create();

    consume(TokenType.READINPUT, err);
    consume(TokenType.LPAREN, err);
    var msg = consume(TokenType.STRINGLITERAL, err);
    consume(TokenType.RPAREN, err);
    return new Stmt.Input(msg);
  }

    public Stmt parseAssignment() {
	return null;
    }

    public Stmt parsePrint() {

	var err =
	    new Report.Builder()
            .errWasFatal()
            .setErrorType("Mathematischer Ausdruck nicht geschlossen")
            .withErrorMsg(
			  "Hey wir waren gerade dabei einen Mathematischen Ausdruck zu parsen, es scheint als haettest du vergessen eine Klammer zu schliesen")
            .url("www.TODO.de")
            .create();

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

	return new Stmt.Print(formatter, exprs);
    }

    public Stmt parseWhileLoop() {

	var err =
	    new Report.Builder()
            .errWasFatal()
            .setErrorType("Fehler beim parsen einer Solange schleife")
            .withErrorMsg(
			  "Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient meinander abzuspichern")
            .addExample(String.format("%s", "Typ: Haus {}"))
            .addExample(String.format(
				      "%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
            .url("TODO.de")
            .create();
	consume(TokenType.WHILE, err);
	var condion = parseExpr();
	consume(TokenType.RPAREN, err);

	var body = parseBlock();

	return new Stmt.While(condion, body);
    }

    public Stmt parseReturn() {

	var err =
	    new Report.Builder()
            .errWasFatal()
            .setErrorType("Fehler beim parsen eines DatenTypes!")
            .withErrorMsg(
			  "Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient meinander abzuspichern")
            .addExample(String.format("%s", "Typ: Haus {}"))
            .addExample(String.format(
				      "%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
            .url("TODO.de")
            .create();

	var keyword = consume(TokenType.RETURN, err);
	Expr expr = parseExpr();
	return new Stmt.Return(keyword, expr);
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
    public Stmt parseImport() {

	next();

	var err =
	    new Report.Builder()
            .errWasFatal()
            .setErrorType(
			  "Wir sind uns nicht ganz sicher welche Bibolotheken du meinst!")
            .withErrorMsg(
			  "M dem '#benutze' Befehl kannst du andere Datein und externe Bibilotheken in deinem Programm benuzten.")
            .addExample(String.format("%s", "#benutze ['Mathe', 'Eingabe']"))
            .url("TODO.de")
            .create();

	var libs = new ArrayList();

	consume(TokenType.LBRACKET, err);
	while (!check(TokenType.RBRACKET)) {
	    libs.add(consume(TokenType.STRINGLITERAL, err));
	    consume(TokenType.COMMA, err);
	}
	consume(TokenType.RBRACKET, err);
	return new Stmt.Import(libs);
    }

    public Stmt parseIfStmt() {

	var err =
	    new Report.Builder()
            .errWasFatal()
            .setErrorType("Fehle beim parsen einer Verzweigung")
            .withErrorMsg(
			  "M dem wenn befehl kannst du entscheiden ob und wann eine bestimmte Stelle in deinem Programm ausgefuehrt wird!")
            .addExample(String.format("%s", "wenn foo > 0 {}"))
            .addExample(String.format("%s", "wenn 2 > 0 {}"))
            .url("TODO.de")
            .create();

	consume(TokenType.IF, err);

	// NOTE("wenn" keyword is already identified)
	Expr condion = parseExpr();

	var block = parseBlock();
	return new Stmt.If(condion, null, null);
    }

    public Stmt parseStructDecl() {

	// NOTE(Simon): we arrive here after the Typ Keyword
	next();

	var err =
	    new Report.Builder()
            .errWasFatal()
            .setErrorType("Fehler beim parsen eines DatenTypes!")
            .withErrorMsg(
			  "Um komplexe Programme zu vereinfachen kannst du deine eigenen Datentypen definieren. Das erlaubt dir Daten effizient meinander abzuspichern")
            .addExample(String.format("%s", "Typ: Haus {}"))
            .addExample(String.format(
				      "%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
            .url("TODO.de")
            .create();

	consume(TokenType.COLON, err);
	var structName = consume(TokenType.IDEN, err);

	consume(TokenType.STARTBLOCK, err);

	var arguments = new ArrayList();
	while (!check(TokenType.ENDBLOCK)) {

	    Token varName = consume(TokenType.IDEN, err);
	    consume(TokenType.COLON, err);

	    Token typeName = consume(TokenType.IDEN, err);
	    consume(TokenType.COMMA, err);

	    arguments.add(new Stmt.Class.Attribute(varName, typeName));
	}
	consume(TokenType.ENDBLOCK, err);

	// TODO(Simon): After we parse an impl block should we associate the methods
	// wh the right class?
	// Maybe do in seperate pass?
	return new Stmt.Class(structName, null, arguments);
    }

    // TODO(Simon): add desugared increment in the body
    // TODO(Simon): check if range for the loop is valid
public Stmt parseForLoop() {

    var err = new Report.Builder()
                  .errWasFatal()
                  .setErrorType("Fehler beim parsen einer fuer schleife")
                  .withErrorMsg(
                      "Schleifen erlauben es dir Code mehrmals auszufuerhren")
                  .addExample(String.format("%s", "variablen_name := Wert"))
                  .addExample(String.format("%s", "test := (4 + 2)"))
                  .url("TODO.de")
                  .create();

    consume(TokenType.FOR, err);
    var loopVar = consume(TokenType.IDEN, err);
    consume(TokenType.VARDEF, err);

    var start = consume(TokenType.NUMBERLITERAL, err);
    consume(TokenType.UNTIL, err);
    var end = consume(TokenType.NUMBERLITERAL, err);

    var left = new Expr.Literal(start);
    var right = new Expr.Literal(end);

    var operator = new Token(TokenType.LESSEQUAL);
    var condion = new Expr.Binary(left, operator, right);

    var body = parseBlock();

    return new Stmt.While(condion, body);
  }

  public Stmt parseVarDef() {

    var err =
        new Report.Builder()
            .errWasFatal()
            .setErrorType("Fehler beim parsen einer Variablen Defintion")
            .withErrorMsg(
                "Mhilfe von Variablen kannst du Daten im Laufe deines Programmes speichern um diese an einem spaetern Zepunkt wieder zu verwenden")
            .addExample(String.format("%s", "variablen_name := Wert"))
            .addExample(String.format("%s", "test := (4 + 2)"))
            .url("TODO.de")
            .create();

    // we assume that we peeked ahead to find the :=  Symbol
    Token typeName = null;
    Token varName = consume(TokenType.IDEN, err);

    if (peek().getType() != TokenType.VARDEF) {
      consume(TokenType.VARDEF, err);
    } else if (peek().getType() == TokenType.COLON) {
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

  public boolean matchAny(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        next();
        return true;
      }
    }
    return false;
  }

  public boolean check(TokenType type) {
    if (!hasNext())
      return false;
    return peek().getType() == type;
  }
}

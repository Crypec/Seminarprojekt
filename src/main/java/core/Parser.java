package core;

import java.util.*;
import util.*;
import com.github.tomaslanger.chalk.*;

// TODO(Simon): add parsing for function calls
// TODO(Simon): finish parsing blocks

// TODO(Simon): make err messages better and more descriptive. Maybe we should handle more cases with their own err msg
public class Parser extends Iter<Token> {

    public Parser(Iter<Token> it) {
        super(it.getBuffer()); // TODO(Simon): write better consturctor
    }

    public List<Stmt> parse() {

        var ast = new ArrayList();
        if (check(TokenType.IMPORT)) {
            var libs = parseImport();
            ast.add(libs); // NOTE(Simon): we have to resolve this later to parse all sourcefiles, should be fairly easy to thread this
        }
        while (this.hasNext())   {
            var stmt = parseStmt();
            ast.add(stmt);
        } 
        return ast;
    }

    public void sync() {
        while (hasNext()) {
            switch (peek().getType()) {
            case CLASS, FUNCTION, VARDEF, FOR, WHILE:
                return;
            }
            next();
        }
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
		return new Expr.Assign(name, value, null); // TODO(Simon): Look into required args constructor when using lombok
	    } else if (ASTNode instanceof Expr.Get) {
		var getNode = (Expr.Get)ASTNode;
		return new Expr.Set(getNode.getObject(), getNode.getName(), value);
	    }
	    System.out.printf("%s %s",Chalk.on("[DEBUG ERROR]"), "Invalid Assignment target!!");

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

	while (matchAny(TokenType.NOT, TokenType.NOTEQUAL)) {
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
	while (matchAny(TokenType.MULTIPLY, TokenType.DIVIDE)) {
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
		    .errMsg("Nach einem Punkt haben wir den namen des Feldes eines DatenTypen erwartet!")
		    .example("foo.bar")
		    .example("person.name")
		    .example("konto.guthaben")
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
	var args = new ArrayList();

	if (!check(TokenType.RPAREN)) {
	    do {
		args.add(parseExpr());
	    } while (matchAny(TokenType.COMMA));
	}
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
	var paren = consume(TokenType.RPAREN, err);
	return new Expr.Call(callee, paren, args);
    }

    private Expr parsePrimary() {

	if (matchAny(TokenType.FALSE)) return new Expr.Literal(false);
	if (matchAny(TokenType.TRUE)) return new Expr.Literal(true);
	if (matchAny(TokenType.NULL)) return new Expr.Literal(null);
	if (matchAny(TokenType.STRINGLITERAL, TokenType.NUMBERLITERAL)) {
	    return new Expr.Literal(previous().getLiteral());  
	} 
	if (matchAny(TokenType.SELF)) return new Expr.Self(previous());

	if (matchAny(TokenType.IDEN)) return new Expr.Variable(previous());

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
	var err = Report.builder()
	    .wasFatal(true)
	    .errType("Unerwarteter Token!")
	    .errMsg("An dieser Stelle haben wir einen Mathematischen ausdruck erwartet!")
	    .example("(a + 3)")
	    .example("((42 - 3) - 2)")
	    .example("(foo(2) -2)")
	    .url("TODO")
	    .build();
	System.out.println(err);
	err.sync();
	System.out.println(Chalk.on("[Debug]").green().bold() + ":: This should be unreachable because of the exception thrown in the line before. Internal compiler error!");
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
			.url("TODO.de")
			.build();
		    System.out.println(err);
		    err.sync();
		    yield null; // unreachable because we throw an exception
		}
		};
            
	    }
	} catch(Report.Error e) {
	    e.printStackTrace(); // TODO(Simon): remove for debuggging only!!
	    sync();
	    System.out.println(Chalk.on("[Debug]").green().bold() + ":: Parsing failed, need to backtrack!");
	}
	System.out.println(Chalk.on("[Debug]").green().bold() + ":: This should be unreachable because of the exception thrown in the line before. Internal compiler error!");
	return null;
    }


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
		else yield parseExprStmt();
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

    public Stmt.StructDecl parseStructDecl() {

	// NOTE(Simon): we arrive here after the Typ Keyword

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
            .example(String.format("%s", "Typ: Person {name :Text,\nalter: Zahl,\n}"))
            .url("TODO.de")
            .build();

	consume(TokenType.CLASS, err);
	consume(TokenType.COLON, err);
	var structName = consume(TokenType.IDEN, err);

	consume(TokenType.STARTBLOCK, err);

	var arguments = new ArrayList();
	while (!check(TokenType.ENDBLOCK)) {

	    Token varName = consume(TokenType.IDEN, err);
	    consume(TokenType.COLON, err);

	    Token typeName = consume(TokenType.IDEN, err);
	    consume(TokenType.COMMA, err);

	    arguments.add(new Stmt.StructDecl.Member(varName, typeName));
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

	var condion = new Expr.Binary(start, new Token(TokenType.LESS), end);

	var incrementExpr = new Expr.Binary(new Expr.Literal(loopVar.getLexeme()), new Token(TokenType.PLUS), new Expr.Literal(1));
	var incrementAssingment = new Expr.Assign(loopVar, incrementExpr, null);

	var body = parseBlock();
	body.getStatements().add(new Stmt.Expression(incrementAssingment));
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

	Token varName = consume(TokenType.IDEN, err);

	Token type = null;
	if (check(TokenType.VARDEF)) {
	    consume(TokenType.VARDEF, err);
	} else if (check(TokenType.COLON)) {
	    // user provided type information, variable is typed
	    consume(TokenType.COLON, err);
	    type = consume(TokenType.IDEN, err);
	    consume(TokenType.EQUALSIGN, err);
	} else {
	    consume(TokenType.VARDEF, err); // FIXME(Simon): This is only a short fix to provide a error message
	}

	Expr value = parseExpr();
	consume(TokenType.SEMICOLON, err);
	return new Stmt.VarDef(varName, type, value);
    }

    private Token consume(TokenType type, Report err) {
	if (check(type)){
	    return next();   
	}
	err.setToken(peek());
	System.out.println(err);
	err.sync();
	System.out.println(Chalk.on("[Debug]").green().bold() + ":: This should be unreachable because of the exception thrown in the line before. Internal compiler error!");
	return null; // unreachable code becase sync will throw an execption
    }

    private Token consume(TokenType type, Report err, String errMsg) {
	if (check(type)) {
	    return next();
	} else {
	    err.setToken(peek());
	    err.setErrMsg(errMsg);
	    System.out.println(err);
	    err.sync();
	}
	System.out.println(Chalk.on("[Debug]").green().bold() + ":: This should be unreachable because of the exception thrown in the line before. Internal compiler error!");
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
	return type == peek().getType();
    }
}

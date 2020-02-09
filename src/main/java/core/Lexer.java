package core;

import java.util.*;
import util.*;

public class Lexer {

    private final String fileName;
    private final String buffer;

    private int cursor = 0;

    private int start = 0;
    private int line = 1;

	public Lexer(String buffer, String fileName) {
		this.buffer = buffer;
		this.fileName = fileName;
    }

    public List<Token> tokenize() {
	var tokenStream = new ArrayList();
	while (this.hasNext()) {
	    start = cursor;

	    var c = this.next();
	    var token = switch (c) {
	    case '(': yield buildToken(TokenType.LPAREN);
	    case ')': yield buildToken(TokenType.RPAREN);
	    case '[': yield buildToken(TokenType.LBRACKET);
	    case ']': yield buildToken(TokenType.RBRACKET);
	    case '{': yield buildToken(TokenType.STARTBLOCK);
	    case '}': yield buildToken(TokenType.ENDBLOCK);
	    case ',': yield buildToken(TokenType.COMMA);
	    case ';': yield buildToken(TokenType.SEMICOLON);
	    case ':': {
		if (match('=')) yield buildToken(TokenType.VARDEF);
		if (match(':')) yield buildToken(TokenType.COLONCOLON);
		else yield buildToken(TokenType.COLON);
	    }
	    case '.': {
		if (match('.')) yield buildToken(TokenType.UNTIL); // NOTE(Simon): Does currently not because of the way we parse numbers the first dot gets consumed by the getNum function
		else yield buildToken(TokenType.DOT);
	    }
	    case '-': {
		if (match('>')) yield buildToken(TokenType.ARROW);
		else yield buildToken(TokenType.MINUS);
	    }
	    case '+': yield buildToken(TokenType.PLUS);
	    case '*': yield buildToken(TokenType.MULTIPLY);
	    case '%': yield buildToken(TokenType.MODULO);
	    case '/': {
		if (match('/')) while (peek() != '\n' && hasNext()) next();
		else yield buildToken(TokenType.DIVIDE);
	    }
	    case '!': {
		if (match('=')) yield buildToken(TokenType.NOTEQUAL);
		else yield buildToken(TokenType.NOT);
	    }
	    case '=': {
		if (match('=')) yield buildToken(TokenType.EQUALEQUAL);
		if (match('>')) yield buildToken(TokenType.GREATEREQUAL);
		if (match('>')) yield buildToken(TokenType.LESSEQUAL);
		else yield buildToken(TokenType.EQUALSIGN);
	    }
	    case '>': {
		if (match('=')) yield buildToken(TokenType.GREATEREQUAL);
		else yield buildToken(TokenType.GREATER);
	    }
	    case '<': {
		if (match('=')) yield buildToken(TokenType.LESSEQUAL);
		else yield buildToken(TokenType.LESS);
	    }
	    case '"': yield getStringLiteral();
	    case ' ', '\r', '\t': yield null; // skip all whitespace
	    case '\n': line++; yield null;
	    default: {
		if (Character.isDigit(c)) yield getNumLiteral();
		if (Character.isLetter(c) || c == '#') yield getIden(); // check for hashtag because compiler internal functions are prefixed with a hashtag
		else {
			Report.builder()
				.wasFatal(true)
				.errType("Zeichen nicht erlaubt")
				.errMsg(String.format("Du scheinst ein ein Zeichen zu benutzen das nicht erlaubt ist: %s", c))
				.url("www.TODO.de")
				.build()
				.print();
			yield null;
		}
		}
	    };
	    if (token != null) tokenStream.add(token);
	}
	return tokenStream;
    }

    public Token getIden() {
		while (Character.isDigit(peek()) || Character.isLetter(peek()) || peek() == '_') next();
		String iden = buffer.substring(start, cursor);
		return buildToken(TokenType.match(iden));
    }

    public Token getStringLiteral() {
	while (peek() != '"' && hasNext()) {
	    if (peek() == '\n')
		line++;
	    next();
	}

	// TODO(Simon):
	if (!hasNext()) {

	    var errLocation = Token.builder()
		.type(TokenType.STRINGLITERAL)
		.lexeme(buffer.substring(start, cursor))
			.fileName(fileName)
			.line(line)
			.start(start)
			.end(cursor)
			.build();

	    Report.builder()
			.wasFatal(true)
			.errType("Text nicht beendet")
			.errMsg("Du hast einen nicht erlaubten Token verwendent. Um mehr ueber die namensgebungkonvention in dieser Sprache zu erfahren folge diesem Link")
			.url("www.TODO.de")
			.token(errLocation)
			.build()
			.print();
	    return null;
	}
	next();

	String literal = buffer.substring(start, cursor);
	return buildToken(TokenType.STRINGLITERAL, literal);
    }

    public Token getNumLiteral() {
	while (Character.isDigit(peek())) next();

	if (peek() == '.' && Character.isDigit(peekNext())) {
	    next();
	    while (Character.isDigit(peek())) next();
	} else if (peek() == '.' && peekNext() == '.') {
	    //FIXME(Simon): return until iterator and num
	}
	Double literal = 0.0;  
	try {
	    literal = Double.parseDouble(buffer.substring(start, cursor));
	} catch (Exception e) {
	    Report.builder()
		.wasFatal(true)
		.errType("Zahl nicht richtig formatiert")
		.errMsg("Wir hatten ein Problem beim Parsen einer Zahl im Lexer, achte darauf dass du zahlen richtig formatierst!")
		.url("www.TODO.de")
		.token(null)
		.build()
		.print();
	}
	return buildToken(TokenType.NUMBERLITERAL, literal);
    }

    public static Double parseNum(String strNum) {
	return Double.parseDouble(strNum);
    }

    public boolean hasNext() { return this.buffer.length() > cursor; }

    public char next() { return this.buffer.charAt(cursor++); }

    public char peek() {
	if (!hasNext()) return '\0';
	return buffer.charAt(cursor);
    }

    public char peekNext() {
	if (cursor + 1 >= buffer.length()) return '\0';
	return this.buffer.charAt(cursor++);
    }

    public boolean match(char expected) {
	if (!hasNext()) return false;
	if (buffer.charAt(cursor) != expected) {
	    return false;  
	}  else {
	    next();
	    return true;
	}
    }

    private Token buildToken(TokenType type) { return buildToken(type, Optional.empty()); }

    private Token buildToken(TokenType type, Object literal) {
	return Token.builder()
	    .type(type)
	    .lexeme(buffer.substring(start, cursor))
	    .fileName(fileName)
	    .line(line)
	    .start(start)
	    .end(cursor)
	    .literal(Optional.of(literal))
	    .build();
    }
}

package core;

import java.util.*;
import util.*;

public class Lexer {

    private final String fileName;
    private final String source;

    private int cursor = 0;
    private int start = 0;
    private int line = 1;

    public Lexer(String source, String fileName) {
	this.source = source;
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
		if (peek() == '=') yield buildToken(TokenType.VARDEF);
		if (peek() == ':') yield buildToken(TokenType.COLONCOLON);
		else buildToken(TokenType.COLON);
	    }
	    case '-': {
		if (peek() == '>') yield buildToken(TokenType.ARROW);
		else yield buildToken(TokenType.MINUS);
	    }
	    case '+': yield buildToken(TokenType.PLUS);
	    case '*': yield buildToken(TokenType.MULTIPLY);
	    case '/': {
		if (peek() == '/')
		    while (peek() != '\n' && hasNext()) next();
		else yield buildToken(TokenType.DIVIDE);
	    }
	    case '!': {
		if (peek() == '=') yield buildToken(TokenType.NOTEQUAL);
		else yield buildToken(TokenType.NOT);
	    }
	    case '=': {
		if (peek() == '=')
		    yield buildToken(TokenType.EQUALEQUAL);
		if (peek() == '>') yield buildToken(TokenType.GREATEREQUAL);
		if (peek() == '>') yield buildToken(TokenType.LESSEQUAL);
		else yield buildToken(TokenType.EQUALSIGN);
	    }
	    case '>': {
		if (peek() == '=') yield buildToken(TokenType.GREATEREQUAL);
		else yield buildToken(TokenType.GREATER);
	    }
	    case '<': {
		if (peek() == '=') yield buildToken(TokenType.LESSEQUAL);
		else yield buildToken(TokenType.LESS);
	    }
	    case '"': yield getStringLiteral();
	    case ' ', '\r', '\t': yield null; // skip all whitespace
	    case '\n': line++; yield null;
	    default: {
		if (Character.isDigit(c))
		    yield getNumLiteral();
		if (Character.isLetter(c) || peek() == '#') yield getIden(); // check for hashtag because compiler internal functions are prefixed with a hashtag
		else
		    yield null; // TODO(Simon): report error if invalid char is found
	    }
	    };
	    if (token != null) tokenStream.add(token);
	}
	return tokenStream;
    }

    public Token getIden() {
	while (Character.isDigit(peek()) || Character.isLetter(peek())) next();
	String iden = source.substring(start, cursor);
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

	    var errLocation = new Token.Builder()
		.filename(fileName)
		.withType(TokenType.STRINGLITERAL)
		.lexeme(source.substring(start, cursor))
		.position(start, cursor)
		.build();

	    var err =
		new Report.Builder()
		.errWasFatal()
		.setErrorType("Text nicht beendet")
		.withErrorMsg("Es scheint als haettest du vergessen einen Text block zu schliessen.")
		.url("www.TODO.de")
		.atToken(errLocation)
		.create();
	    System.out.println(err);
	    return null;
	}
	next();

	String literal = source.substring(start +1, cursor - 1);
	return buildToken(TokenType.STRINGLITERAL, literal);
    }

    public Token getNumLiteral() {
	while (Character.isDigit(peek()))
	    next();

	if (peek() == '.' && Character.isDigit(peekNext())) {
	    next();
	    while (Character.isDigit(peek())) next();
	}
	Double literal = Double.parseDouble(source.substring(start, cursor));
	return buildToken(TokenType.NUMBERLITERAL, literal);
    }

    public static Double parseNum(String strNum) {
	return Double.parseDouble(strNum);
    }

    public boolean hasNext() { return this.source.length() > cursor; }

    public char next() { return this.source.charAt(cursor++); }

    public char peek() {
	if (!hasNext()) return '\0';
	return source.charAt(cursor);
    }

    public char peekNext() {
	if (cursor + 1 >= source.length()) return '\0';
	return this.source.charAt(cursor++);
    }

    private Token buildToken(TokenType type) { return buildToken(type, null); }

    private Token buildToken(TokenType type, Object literal) {
	return new Token.Builder()
	    .filename(fileName)
	    .lexeme(source.substring(start, cursor))
	    .position(start, cursor)
	    .withType(type)
	    .withLiteral(literal)
	    .build();
    }
}

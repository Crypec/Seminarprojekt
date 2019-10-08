package konrad;

import java.util.*;
import konrad.util.*;

public class Parser {

    private List<Token> buffer;
    private int current = 0;

    public Parser(List<Token> buffer) {
	this.buffer = buffer;
    }
    
    private Expr expression() {
	return equality();
    }

    private Expr equality() {

	var ASTNode = comparison();

	while (match(TokenType.EQUALEQUAL, TokenType.NOTEQUAL)) {
	    var operator = previous();
	    var right = comparison();
	    ASTNode = new Expr.Binary(ASTNode, operator, right);
	}
	return ASTNode;
    }

    private Expr comparison() {
	var ASTNode = addition();

	while (match(TokenType.GREATER, TokenType.GREATEREQUAL, TokenType.LESS, TokenType.LESSEQUAL)) {
	    var operator = previous();
	    var right = addition();
	    ASTNode = new Expr.Binary(ASTNode, operator, right);
	}
	return ASTNode;
    }
    
    private Expr addition() {
	var ASTNode = multiplication();

	while (match(TokenType.GREATER, TokenType.GREATEREQUAL, TokenType.LESS, TokenType.LESSEQUAL)) {
	    var operator = previous();
	    var right = multiplication();
	    ASTNode = new Expr.Binary(ASTNode, operator, right);
	}
	return ASTNode;
    }

    private Expr multiplication() {
	var ASTNode = unary();

	while (match(TokenType.DIVIDE, TokenType.MULTIPLY)) {
	    var operator = previous();
	    var right = unary();
	    ASTNode = new Expr.Binary(ASTNode, operator, right);
	}
	return ASTNode;
    }

    private Expr unary() {
	if (match(TokenType.NOT, TokenType.MINUS)) {
	    var operator = previous();
	    var right = unary();
	    return new Expr.Unary(operator, right);
	}
	return primary();
    }

    private Expr primary() {

	
	
	if (match(TokenType.FALSE)) return new Expr.Literal(false);
	if (match(TokenType.TRUE)) return new Expr.Literal(true);
	if (match(TokenType.NUMBERLITERAL, TokenType.STRINGLITERAL)) return new Expr.Literal(previous().getLiteral());
	//NOTE(Simon): maybe we should support NULL values in the future, i still thinks its a good
	//NOTE(Simon): idea to just initalize every var with a default value
	//if (match(TokenType.NULL)) return new Expr.Literal(null); 


	if (match(TokenType.LPAREN)) {
	    var ASTNode = expression();
	    consume(TokenType.RPAREN, "Nach einem Ausdruck haben wir eine ')' erwartet");
	    return new Expr.Grouping(ASTNode);
	}
	return new Expr.Literal(false); // just to please the java compiler
    }
    

    private boolean match(TokenType... types) {
	for (var type : types) {
	    if (check(type)) {
		next();
		return true;
	    }
	}
	return false;
    }

    private boolean check(TokenType type) {
	if (!hasNext()) return false;
	return peek().getType() == type;
    }
    
    private Token consume(TokenType type, String msg) {
	if (check(type)) return next();
	return new Token.Builder().build();
    }

    private Token next() {
	if (!hasNext()) current++;
	return previous();
    }

    private boolean hasNext() {
	return buffer.size() > current;
    }

    private Token peek() {
	return buffer.get(current);
    }

    private Token previous() {
	return buffer.get(current -1);
    }
}

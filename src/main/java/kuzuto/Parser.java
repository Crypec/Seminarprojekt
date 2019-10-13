package kuzuto;

import kuzuto.util.*;


// TODO(Simon): Refactor to Iter  
public class Parser {

    private static class ParseError extends RuntimeException{}

    private static Expr multiplication(Iter<Token> it) {

	Expr ASTNode = unary(it);

	while (matchAny(it, TokenType.DIVIDE, TokenType.MULTIPLY)) {
	    Token operator = it.previous();
	    Expr right = unary(it);
	    ASTNode = new Expr.Binary(ASTNode, operator, right);
	}
	return ASTNode;
    }
    
    private static Expr addition(Iter<Token> it) {
	Expr ASTNode = multiplication(it);

	while (matchAny(it, TokenType.MINUS, TokenType.PLUS)) {
	    Token operator = it.previous();
	    Expr right = multiplication(it);
	    ASTNode = new Expr.Binary(ASTNode, operator, right);
	}
	return ASTNode;
    }
    
    private static Expr comparison(Iter<Token> it) {

	Expr ASTNode = addition(it);

	while (matchAny(it, TokenType.GREATER, TokenType.GREATEREQUAL, TokenType.LESS, TokenType.LESSEQUAL)) {
	    Token operator = it.previous();
	    Expr right = addition(it);
	    ASTNode = new Expr.Binary(ASTNode, operator, right);

	}
	return ASTNode;
    }

    private static Expr equality(Iter<Token> it) {

	Expr ASTNode = comparison(it);

	while (matchAny(it, TokenType.NOTEQUAL, TokenType.EQUALEQUAL)) {
	    Token operator = it.previous();
	    Expr right = comparison(it);
	    ASTNode = new Expr.Binary(ASTNode, operator, right);
	}
	return ASTNode;
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
	if (matchAny(it, TokenType.FALSE)) return new Expr.Literal(false);
	if (matchAny(it, TokenType.TRUE)) return new Expr.Literal(true);

	if (matchAny(it, TokenType.NUMBERLITERAL, TokenType.STRINGLITERAL)) {
	    return new Expr.Literal(it.previous().getLiteral());
	}

	if (matchAny(it, TokenType.LPAREN)) {
	    Expr expr = expression(it);
	    // consume(TokenType.RPAREN, "Zutoko erwartet eine ) nach einem Ausdruck!");
	    return new Expr.Grouping(expr);
	}
	return new Expr.Literal(true); //make java happy for now
	    }

    private static Expr expression(Iter<Token> it) {
	return equality(it);
    }

    public static boolean matchAny(Iter<Token> it, TokenType... types) {
	for (var type : types) {
	    if (check(type, it)) {
		it.next();
		return true;
	    } 
	}
	return false; 
    }

    public static boolean check(TokenType type, Iter<Token> it) {
	return !it.hasNext() ? false : it.peek().getType() == type;
    }
}

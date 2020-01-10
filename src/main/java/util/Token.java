package util;

import com.google.gson.*;

import java.util.*;
import core.Lexer;

public class Token {

    // also these fields should't be public :D
    private String lexeme;
    private TokenType type;
    private Object literal;
    private MetaData meta;
    
    public Token(String lexeme, MetaData meta, TokenType type, Object literal) {
	this.lexeme = lexeme;
	this.meta = meta;
	this.type = type;
	this.literal = literal;

	// NOTE(Simon): Do we really need this?
	// NOTE(Simon): Because we also match Literals in the parser. We shouldn't be doing the same work twice.
	if (this.literal == null) {
	    this.literal = switch (this.type) {
	    case STRINGLITERAL -> lexeme;
	    case NUMBERLITERAL -> Lexer.parseNum(lexeme);
	    case TRUE -> true;
	    case FALSE -> false; 
	    default -> null;
	    };
	}
    }

    public Token(TokenType type) {
	this.type = type;
	this.lexeme = null;
	this.literal = null;
	this.meta = new MetaData("");
    }

    public static class Builder {
	
	private String lexeme;
	private TokenType type;
	private Object literal = null;
	private MetaData meta;

	public Builder filename(String filename) {
	    this.meta = new MetaData();
	    this.meta.setFilename(filename);
	    return this;
	}

	public Builder line(int line) {
	    this.meta.setLine(line);
	    return this;
	}

	public Builder position(int start, int end) {
	    this.meta.setStartPos(start);
	    this.meta.setEndPos(end);
	    return this;
	}
	
	public Builder lexeme(String lexeme) {
	    this.lexeme = lexeme;
	    return this;
	}


	public Builder withType(TokenType type) {
	    this.type = type;
	    return this;
	}

	public Builder withLiteral(Object literal) {
		this.literal = literal;
		return this;
	}

	public Token build() {
	    return new Token(this.lexeme, this.meta, this.type, this.literal);
	}
    }


    public static boolean isSingleCharToken(char s) {
	return switch (s) {
	case '{', '}', '(', ')', '[', ']', '.', '+', '*', '%', '<', ':', ',' -> true;
	default  -> false;
	};
    }

    public static boolean isDoubleCharToken(char s) {
	return switch (s) {
	case '-', ':', '=', '!', '<', '>' -> true;
	default -> false;
	};
    }

    public static boolean endOfExprTokenNeeded(Token token) {
	return switch (token.getType()) {
	case WHILE, FOR, FUNCTION, IF, ELSE -> false; 
	default -> true;
	};
    }

    public static boolean validForExpr(Token t) {
	return switch(t.type) {
	case NUMBERLITERAL, STRINGLITERAL, TRUE, FALSE, PLUS, MINUS, MULTIPLY, DIVIDE, AND, OR, NOT -> true;
	default -> false;
	};
    }
    
    public void setType(TokenType type) {
	this.type = type;
    }

    public void setLexeme(String lexeme) {
	this.lexeme = lexeme;
    }
    
    public void setLiteral(Object literal) {
	this.literal = literal; 
    }

    public void setMeta(MetaData meta) {
	this.meta = meta;
    }
    
    public TokenType getType() {
	return this.type;
    }

    public static void printAll(List<Token> tokenStream) {
	for (Token t : tokenStream) {
	    System.out.println(t);
	}
    }
    
    public Object getLiteral() { return this.literal; }

    public String getLexeme() { return this.lexeme; }

    public MetaData getMeta() { return this.meta; }

    public boolean equals(Token t) {
	return t.type == t.getType();
    }

    // TODO(Simon): add operator precedence of boolean operators
    public int getPrecedence() {
	return switch (this.type) {
	case MULTIPLY, DIVIDE -> 3;
	case PLUS, MINUS -> 2;
	default -> 0;
	};
    }

    // Add operators for boolean operations
    public boolean isOperator() {
	return switch (this.type) {
	case MULTIPLY, DIVIDE, PLUS, MINUS -> true;
	default -> false;
	};
    }

    // NOTE(Simon): check if the "NOT" operator is left assozioative
    public boolean isLeftAssoziative() {
	return false;
    }
    
    @Override
    public String toString() {
	return new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}

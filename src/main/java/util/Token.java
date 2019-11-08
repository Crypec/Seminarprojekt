package util;

import com.google.gson.*;

import java.util.*;
import kuzuto.Lexer;

public class Token {

    // also these fields should't be public :D
    private String lexeme;
    private TokenType type;
    private Object literal;
    private MetaData meta;

    public Token(String lexeme, MetaData meta) {
	this.lexeme = lexeme;
	this.meta = meta;
	this.type = Token.matchType(lexeme);

	this.literal = switch (this.type) {
        case STRINGLITERAL -> lexeme;
	case NUMBERLITERAL -> Lexer.parseNum(lexeme);
	case TRUE -> true;
	case FALSE -> false; 
	default -> null;
	};
    }

    public Token(TokenType type) {
	this.type = type;
	this.lexeme = null;
	this.literal = null;
	this.meta = new MetaData("");
    }

    public static class Builder {
	
	private String lexeme;
	private Object literal;
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

	public Token build() {
	    return new Token(this.lexeme, this.meta);
	}
    }


    public static TokenType matchType(String s) {
	if (isNumeric(s)) {
	    return TokenType.NUMBERLITERAL;
	}
	return switch (s) {
		// keywords
	    case "importiere" -> TokenType.IMPORT;
	    case "fun" -> TokenType.FUNCTION;
	case "solange" -> TokenType.WHILE;
	case "für" -> TokenType.FOR;
	case "wenn" -> TokenType.IF;
	case "sonst" -> TokenType.ELSE;
	case "rückgabe" -> TokenType.RETURN;
	case "Typ" -> TokenType.CLASS;
	case "bis" -> TokenType.UNTIL;

	// Compiler native functions
	case "#eingabe" -> TokenType.READINPUT;
	case "#ausgabe" -> TokenType.PRINT;

	//basic types
	case "Zahl" -> TokenType.NUMBERTYPE;
	case "Text" -> TokenType.STRINGTYPE;
	case "Bool" -> TokenType.BOOLEANTYPE;

	//const declarations
	case "konst" -> TokenType.CONST;

	//boolean operations
	case "wahr" -> TokenType.TRUE;
	case "falsch" -> TokenType.FALSE;

	case "&&", "und" -> TokenType.AND;

	case "||", "oder" -> TokenType.OR;

	case "!" -> TokenType.NOT;

	//comparisons
	case "==", "gleich" -> TokenType.EQUALEQUAL;
	case "!=" -> TokenType.NOTEQUAL;

	case "<=" -> TokenType.LESSEQUAL;
	case ">=" -> TokenType.GREATEREQUAL;

	case "<" -> TokenType.LESS;
	case ">"  -> TokenType.GREATER;

	//assignment operators
	case ":=" -> TokenType.VARDEF;

	case "->" -> TokenType.ARROW;
	
	//other single char tokens
	case "{" -> TokenType.STARTBLOCK;
	case "}" -> TokenType.ENDBLOCK;
	case "(" -> TokenType.LPAREN;
	case ")" -> TokenType.RPAREN;
	case "[" -> TokenType.BRACKETLEFT;
	case "]" -> TokenType.BRACKETRIGHT;
	case ":" -> TokenType.COLON;
	case "." -> TokenType.DOT;
	case "=" -> TokenType.EQUALSIGN;
	case "," -> TokenType.COMMA;

	//Math operators
	case "+" -> TokenType.PLUS;
	case "-" -> TokenType.MINUS;
	case "*" -> TokenType.MULTIPLY;
	case "/" -> TokenType.DIVIDE;
	
	case "%" -> TokenType.MODULO;

	default -> TokenType.SYMBOL;
	};
    }

    public static boolean isSingleCharToken(char s) {
	return switch (s) {
	case '{', '}', '(', ')', '[', ']', '.', '+', '*', '%', '<', ':', ',' -> true;
	default  -> false;
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

    public static boolean isNumeric(String s) {
	try {
	    Double.parseDouble(s);
	} catch (Exception e) {
	    return false;
	}
	return true;
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

    @Override
    public String toString() {
	return new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}

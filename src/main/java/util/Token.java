package konrad.util; 

import java.util.*;

public class Token {

    // also these fields should't be public :D
    public String lexeme;
    public TokenType type;
    public Object value;
    public MetaData metaData;

    public Token(String lexeme) {
	this.type = Token.matchType(lexeme);
	this.lexeme = lexeme;

	this.value = switch (this.type) {
	case STRINGLITERAL -> lexeme;
	case NUMBERLITERAL -> konrad.Lexer.parseNum(lexeme);
	case TRUE -> true;
	case FALSE -> false; 
	default -> null;
	};
    }

    public Token(String lexeme, TokenType type, Object value) {
	this.lexeme = lexeme;
	this.type = type;
	this.value = value;
    }

    public Token(TokenType type) {
	this.lexeme = null;
	this.type = type;
	this. value = null;
	this.metaData = null;
    }

    public static TokenType matchType(String s) {
	return switch (s) {
	    // keywords
	case "importiere" -> TokenType.IMPORT;
	case "fun" -> TokenType.FUNCTION;
	case "solange" -> TokenType.WHILE;
	case "für" -> TokenType.FOR;
	case "wenn" -> TokenType.IF;
	case "dann" -> TokenType.THAN;
	case "sonst" -> TokenType.ELSE;

	//basic types
	case "Zahl" -> TokenType.NUMBERTYPE;
	case "Text" -> TokenType.STRINGTYPE;
	case "bool" -> TokenType.BOOLEANTYPE;

	//const declarations
	case "konst" -> TokenType.CONST;

	//boolean operations
	case "wahr" -> TokenType.TRUE;
	case "falsch" -> TokenType.FALSE;

	case "&&" -> TokenType.AND;

	case "||" -> TokenType.OR;

	case "!" -> TokenType.NOT;

	//comparisons
	case "==", "gleich" -> TokenType.EQUALEQUAL;
	case "!=" -> TokenType.NOTEQUAL;
	case "<" -> TokenType.LESSTHAN;
	case ">"  -> TokenType.GREATERTHAN;

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

    public TokenType getType() {
	return this.type;
    }

    public static void printStream(List<Token> tokenStream) {
	for (Token t : tokenStream) {
	    System.out.println(t);
	}
    }

    public String getLexeme() {
	return this.lexeme;
    }

    public MetaData getMeta() {
	return this.metaData;
    }
    
    public String toString() {
	if (this.value != null) {
	    return String.format("Token: %s [%s :: %s]", this.type.name(), this.lexeme, this.value);
	} else {
	    return String.format("Token: %s [%s]", this.type.name(), this.lexeme);
	}
    }
}

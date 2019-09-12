package konrad.util;

import java.util.HashSet;
import konrad.util.common.*;

public class Token {

    // NOTE(Simon): How are we going to do error messages so that not all of the
    // tokens NOTE(Simon): have to know their filename and position inside their
    // sourcefile
    // also these fields should't be public :D
    public String lexeme;
    public TokenType type;
    public Object value = null;
    public MetaData metaData;

    public Token(TokenType type, String lexeme) {
	this.type = type;
	this.lexeme = lexeme;
    }

    public Token(TokenType type, String lexeme, Object value) {
	this.type = type;
	this.lexeme = lexeme;
	this.value = value;
    }

    // NOTE(Simon): should we allow the use of english keywords?
    public static Token match(String s) {

	if (isNumeric(s)) {
	    double num = Double.parseDouble(s);
	    return new Token(TokenType.NUMBERLITERAL, s, num);
	}

	return switch (s) {
	    // keywords
	case "importiere" -> new Token(TokenType.IMPORT, s);
	case "funktion" -> new Token(TokenType.FUNCTION, s);
	case "solange" -> new Token(TokenType.WHILE, s);
	case "für" -> new Token(TokenType.FOR, s);
	case "wenn" -> new Token(TokenType.IF, s);
	case "dann" -> new Token(TokenType.THAN, s);
	case "sonst" -> new Token(TokenType.ELSE, s);
	case "definiere" -> new Token(TokenType.DEFINE, s);

	//basic types
	case "Zahl" -> new Token(TokenType.NUMBERTYPE, s);
	case "Text" -> new Token(TokenType.STRINGTYPE, s);
	case "Wahrheitswert" -> new Token(TokenType.BOOLEANTYPE, s);

	//const declarations
	case "konst","konstant", "konstante" -> new Token(TokenType.CONST, s);

	//boolean operations
	case "wahr" -> new Token(TokenType.TRUE, s, true);
	case "falsch" -> new Token(TokenType.FALSE, s, false);

	case "UND", "&&" -> new Token(TokenType.AND, s);

	case "ODER", "||" -> new Token(TokenType.OR, s);

	case "NICHT", "!" -> new Token(TokenType.NOT, s);

	//comparisons
	case "==" -> new Token(TokenType.EQUALEQUAL, s);
	case "!=" -> new Token(TokenType.NOTEQUAL, s);
	case "<" -> new Token(TokenType.LESSTHAN, s);
	case ">"  -> new Token(TokenType.GREATERTHAN, s);

	//assignment operators
	case ":=" -> new Token(TokenType.VARDEF, s);
	
	//other single char tokens
	case "{" -> new Token(TokenType.STARTBLOCK, s);
	case "}" -> new Token(TokenType.ENDBLOCK, s);
	case "(" -> new Token(TokenType.PARENLEFT, s);
	case ")" -> new Token(TokenType.PARENRIGHT, s);
	case "[" -> new Token(TokenType.BRACKETLEFT, s);
	case "]" -> new Token(TokenType.BRACKETRIGHT, s);
	case ":" -> new Token(TokenType.COLON, s);
	case "." -> new Token(TokenType.DOT, s);
	case "=" -> new Token(TokenType.EQUALSIGN, s);

	//Math operators
	case "+" -> new Token(TokenType.PLUS, s);
	case "-" -> new Token(TokenType.MINUS, s);
	case "*" -> new Token(TokenType.MULTIPLY, s);
	case "/" -> new Token(TokenType.DIVIDE, s);
	
	case "%", "mod", "modulo" -> new Token(TokenType.MOD, s);

	default -> new Token(TokenType.SYMBOL, s);
	};
    }

    public static boolean isNumeric(String str) {
	// null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        return str.chars().allMatch(Character::isDigit);
    }


    public static boolean isSingleCharToken(char s) {
	return switch (s) {
	case '{', '}', '(', ')', '[', ']', '.', '+', '*', '%', '<' -> true;
	default  -> false;
	};
    }

    public String toString() {
	if (this.value != null) {
	    return String.format("Token: %s [%s :: %s]", this.type.name(), this.lexeme, this.value);
	} else {
	    return String.format("Token: %s [%s]", this.type.name(), this.lexeme);
	}

    }



}

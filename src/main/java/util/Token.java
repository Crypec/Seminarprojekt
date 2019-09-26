package konrad.util; 

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


    // NOTE(Simon): should we allow the use of english keywords?
    public static TokenType matchType(String s) {

	if (isNumeric(s)) {
	    return TokenType.NUMBERLITERAL;
	}
	
	return switch (s) {
	    // keywords
	case "importiere" -> TokenType.IMPORT;
	case "funktion" -> TokenType.FUNCTION;
	case "solange" -> TokenType.WHILE;
	case "für" -> TokenType.FOR;
	case "wenn" -> TokenType.IF;
	case "dann" -> TokenType.THAN;
	case "sonst" -> TokenType.ELSE;
	case "definiere","def" -> TokenType.DEFINE;

	//basic types
	case "Zahl" -> TokenType.NUMBERTYPE;
	case "Text" -> TokenType.STRINGTYPE;
	case "Wahrheitswert" -> TokenType.BOOLEANTYPE;

	//const declarations
	case "konst","konstant", "konstante" -> TokenType.CONST;

	//boolean operations
	case "wahr" -> TokenType.TRUE;
	case "falsch" -> TokenType.FALSE;

	case "UND", "&&" -> TokenType.AND;

	case "ODER", "||" -> TokenType.OR;

	case "NICHT", "!" -> TokenType.NOT;

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
	
	case "%", "mod", "modulo" -> TokenType.MOD;

	default -> TokenType.SYMBOL;
	};
    }

    public static boolean isSingleCharToken(char s) {
	return switch (s) {
	case '{', '}', '(', ')', '[', ']', '.', '+', '*', '%', '<', ':', ',' -> true;
	default  -> false;
	};
    }

    // NOTE(Simon): throwing and catching an exception in java is really costly if we ever try to speed up th compiler we should try to replace this routine with something different
    public static boolean isNumeric(String str) {
	try {
	    Double.parseDouble(str);
	}
	catch (Exception e) {
	    return false;
	}
	return true;
    }


    public TokenType getType() {
	return this.type;
    }


    public String toString() {
	if (this.value != null) {
	    return String.format("Token: %s [%s :: %s]", this.type.name(), this.lexeme, this.value);
	} else {
	    return String.format("Token: %s [%s]", this.type.name(), this.lexeme);
	}
    }
}

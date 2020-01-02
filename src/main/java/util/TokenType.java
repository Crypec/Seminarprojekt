package util;

public enum TokenType {
    // keywords
    IMPORT,
    IDEN,
    FUNCTION,
    CLASS,
    SELF,
    WHILE,
    FOR,
    BREAK,
    UNTIL,
    IF,
    ELSE,
    RETURN,

    // Compiler native functions
    READINPUT,
    PRINT,

    // TODO(Simon): remove primitive types we could desugar them in the parser, this would save use the time to check them seperately in typechecking phase
    // primitive Types
    // STRINGTYPE,
    // NUMBERTYPE,
    // BOOLEANTYPE,

    STRINGLITERAL,
    NUMBERLITERAL,

    COLONCOLON,

    // single char tokens
    STARTBLOCK,
    ENDBLOCK,
    LPAREN,
    RPAREN,
    LBRACKET,
    RBRACKET,
    COLON,
    DOT,
    EQUALSIGN,
    SEMICOLON,
    COMMA,

    EOF, // the EndOfFile token specifies the end of a file, if we reach this token we are finished with parsing

    VARDEF,
    ARROW,

    NULL,

    // boolean operations
    TRUE,
    FALSE,
    AND,
    OR,
    NOT,
    LESS,
    LESSEQUAL,
    GREATER,
    GREATEREQUAL,
    EQUALEQUAL,
    NOTEQUAL,

    // math operators
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO;

    public static TokenType match(String s) {
	return switch (s) {
	    // keywords
	case "fun" -> TokenType.FUNCTION;
	case "solange" -> TokenType.WHILE;
	case "für", "fuer" -> TokenType.FOR;
	case "wenn" -> TokenType.IF;
	case "sonst" -> TokenType.ELSE;
	case "rückgabe", "rueckgabe" -> TokenType.RETURN;
	case "Typ" -> TokenType.CLASS;
	case "selbst" -> TokenType.SELF;
	case "bis" -> TokenType.UNTIL;
	case "stopp" -> TokenType.BREAK;

	// Compiler native functions
	case "#eingabe" -> TokenType.READINPUT;
	case "#ausgabe" -> TokenType.PRINT;
	case "#benutze" -> TokenType.IMPORT;

	case "#Null" -> TokenType.NULL;

	//boolean operations
	case "wahr" -> TokenType.TRUE;
	case "falsch" -> TokenType.FALSE;

	case "und" -> TokenType.AND;

	case "oder" -> TokenType.OR;

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
	case "::" -> TokenType.COLONCOLON;
	
	//other single char tokens
	case "{" -> TokenType.STARTBLOCK;
	case "}" -> TokenType.ENDBLOCK;
	case "(" -> TokenType.LPAREN;
	case ")" -> TokenType.RPAREN;
	case "[" -> TokenType.LBRACKET;
	case "]" -> TokenType.RBRACKET;
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

	default -> TokenType.IDEN;
	};
    }

    public static boolean isNumeric(String s) {
	try {
	    Double.parseDouble(s);
	} catch (Exception e) {
	    return false;
	}
	return true;
    }
}

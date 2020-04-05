package util;

public enum TokenType {
    // keywords
    IMPORT,
    IDEN,
    FUNCTION,
    CLASS,
    IMPL,
    SELF,
    WHILE,
    FOR,
    BREAK,
    UNTIL,
    IF,
    THEN,
    ELSE,
    RETURN,

    // Compiler native functions
    READINPUT,
	WRITE,
    PRINT,

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
	PLACEHOLDER,

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
	case "fn", "fun", "funktion" -> TokenType.FUNCTION;
	case "solange" -> TokenType.WHILE;
	case "für", "fuer" -> TokenType.FOR;
	case "wenn" -> TokenType.IF;
	case "dann" -> TokenType.THEN;
	case "sonst" -> TokenType.ELSE;
	case "rückgabe", "rueckgabe" -> TokenType.RETURN;
	case "Typ" -> TokenType.CLASS;
	case "impl", "implementiere" -> TokenType.IMPL;
	case "selbst" -> TokenType.SELF;
	case "bis" -> TokenType.UNTIL;
	case "stopp" -> TokenType.BREAK;

	// Compiler native functions
	case "#eingabe" -> TokenType.READINPUT;
	case "#schreibe" -> TokenType.WRITE;
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
	case "_" -> TokenType.PLACEHOLDER;
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

	public boolean isComparisonOperator() {
		return switch (this) {
		case EQUALEQUAL -> true;
		case NOTEQUAL -> true;	
		case GREATER -> true;
		case GREATEREQUAL -> true;
		case LESS -> true;
		case LESSEQUAL -> true;
		default -> false;
		};
	}
	
}

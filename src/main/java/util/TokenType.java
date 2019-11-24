package util;

public enum TokenType {
    // keywords
    IMPORT,
    SYMBOL,
    FUNCTION,
    CLASS,
    WHILE,
    FOR,
    UNTIL,
    IF,
    ELSE,
    CONST,
    RETURN,

    // Compiler native functions
    READINPUT,
    PRINT,


    // primitive Types
    STRINGTYPE,
    NUMBERTYPE,
    BOOLEANTYPE,

    STRINGLITERAL,
    NUMBERLITERAL,

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
	if (isNumeric(s)) {
	    return TokenType.NUMBERLITERAL;
	}
	return switch (s) {
	    // keywords
	case "fun" -> TokenType.FUNCTION;
	case "solange" -> TokenType.WHILE;
	case "für" -> TokenType.FOR;
	case "wenn" -> TokenType.IF;
	case "sonst" -> TokenType.ELSE;
	case "rückgabe" -> TokenType.RETURN;
	case "Typ" -> TokenType.CLASS;
	case "bis", ".." -> TokenType.UNTIL;

	// Compiler native functions
	case "#eingabe" -> TokenType.READINPUT;
	case "#ausgabe" -> TokenType.PRINT;
	case "#benutze" -> TokenType.IMPORT;

	//basic types
	// NOTE(Simon): Should we treat them as symbols 
	case "Zahl" -> TokenType.NUMBERTYPE;
	case "Text" -> TokenType.STRINGTYPE;
	case "Bool" -> TokenType.BOOLEANTYPE;

	//const declarations
	// NOTE(Simon): Do we really need those?
	case "konst" -> TokenType.CONST;

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

	default -> TokenType.SYMBOL;
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

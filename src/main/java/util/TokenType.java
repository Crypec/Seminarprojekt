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
    STRINGLITERAL,
    NUMBERTYPE,
    NUMBERLITERAL,
    BOOLEANTYPE,

    // single char tokens
    STARTBLOCK,
    ENDBLOCK,
    LPAREN,
    RPAREN,
    BRACKETLEFT,
    BRACKETRIGHT,
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
}

package konrad.util;

public enum TokenType {
    // keywords
    IMPORT,
    SYMBOL,
    FUNCTION,
    WHILE,
    FOR,
    IF,
    THAN,
    ELSE,
    CONST,

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

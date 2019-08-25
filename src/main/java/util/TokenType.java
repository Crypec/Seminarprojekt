package konrad.util;

public enum TokenType {
    // keywords
    IMPORT, SYMBOL, FUNCTION, WHILE, FOR, IF, THAN,
    ELSE, DEFINE, CONST,

    // primitive Types
    STRINGTYPE, STRINGLITERAL, NUMBERTYPE,
    NUMBERLITERAL, BOOLEANTYPE,

    // single char tokens
    STARTBLOCK, ENDBLOCK, PARENLEFT, PARENRIGHT, BRACKETLEFT, BRACKETRIGHT,
    COLON, DOT, EQUALSIGN,

    // boolean operations
    TRUE, FALSE, AND, OR, NOT, LESSTHAN, GREATERTHAN, EQUALEQUAL, NOTEQUAL,

    // math operators
    PLUS, MINUS, MULTIPLY, DIVIDE, MOD;
}


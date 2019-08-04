public enum TokenType {

    // word tokens
    IMPORT, SYMBOL, FUNCTION, WHILE, FOR, UNTIL, IF, THAN, ELSE, DEFINE,
    CONST, NUMBERTYPE, STRINGTYPE, STRINGLITERAL, NUMBERLITERAL, IMPORT,
    
    // single char token
    DOT, COLON, STARTBLOCK, ENDBLOCK, BRACKETLEFT, BRACKETRIGHT, PARENLEFT, PARENRIGHT,

    // math operators
    PLUS, MINUS, MULTIPLY, DIVIDE, MOD, GREATER, LESS,

    // boolean expressions
    // NOTE(Simon): We could implement the comparisons in the parser but I think it is easier this way
    TRUE, FALSE, AND, OR, NOT, EQUAL, EQUALEQUAL, NOTEQUAL;
    
}

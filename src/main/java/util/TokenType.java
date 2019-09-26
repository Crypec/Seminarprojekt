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
  DEFINE,
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
  ENDOFEXPR,

  VARDEF,
  ARROW,

  // boolean operations
  TRUE,
  FALSE,
  AND,
  OR,
  NOT,
  LESSTHAN,
  GREATERTHAN,
  EQUALEQUAL,
  NOTEQUAL,

  // math operators
  PLUS,
  MINUS,
  MULTIPLY,
  DIVIDE,
  MOD;

  public static boolean isMathOperator(TokenType t) {
      return switch(t) {
      case PLUS, MINUS, MULTIPLY, DIVIDE, MOD -> true;
      default -> false;
      };
  }

  public boolean isMathOperator() {
      return isMathOperator(this);
  }
  
}

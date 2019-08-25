package konrad;

import java.util.ArrayList;
import konrad.util.*;

public class Lexer {

    // NOTE(Simon): this is a brutally simple version of the lexer, there is a
    // great chance we need to rewrite this NOTE(Simon): to focus on Error
    // handling... (making it compatible with taking a sourcefile)
    public static ArrayList<Token> lex(ArrayList<Character> sourceStream) {}

    public static ArrayList<Token> lex(SourceFile source) {

	var sb = new StringBuffer();

	var tokenStream = new ArrayList<Token>();
	while (source.hasNext()) {
	    if (Character.isDigit(c)) {

	    }
	    var token = switch (c) {
	    case 
	    }


	    Char c = source.next();
	    if (Character.isDigit(c)) {
		var token = getNumLiteral(source);
		tokenStream.append(token);
	    } else if (c == '"') {
		var token = getStringLiteral(source);
		tokenStream.append(token);
	    } else if (c == ' ') {
		if (!sb.isEmpty()) {
		    var token = Token.match(sb.toString());
		    tokenStream.append(token);
		} else {
		    sb.append(c);
		}
	    }
	}
    }

    
    public static Token getNumLiteral(SourceFile source) {
	var sb = new StringBuffer();
	sb.append(source.prev());

	while (source.hasNext()) {
	    while (Character.isDigit(source.next()))
		sb.append(source.current());
	}
	/* TODO(Simon): place for custom number parsing such as:
	   # replace dot with comma
	   # custom number seperator
	   # custom number base?
	*/
	Double value = Double.parseDouble(sb.toString());
	// NOTE(Simon): make sure to copy original value of String to lexeme
	return new Token(TokenType.NUMBERLITERAL, sb.toString(), value);
    }

    public static Token getStringLiteral(SourceFile source) {
      var sb = new StringBuffer();
      while (source.hasNext()) {
        char token;
        while ((token = source.next()) != '"') {
          sb.append(token);
        }
      }
      String lexeme = '"' + sb.toString() + '"';
      return new Token(TokenType.STRINGLITERAL, lexeme, sb.toString());
    }
}

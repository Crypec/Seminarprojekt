package core;

import java.util.*;
import util.*;

public class Lexer {

    // TODO(Simon): Right now the lexer does accept all chracters as valid input
    // -> perhaps we should do some basic error reporting right in the lexer
    // FIXME(Simon): check for tokens which are by definition composed of 2
    // characters, for example == or the arrow operator. 
    public static ArrayList<Token> tokenizeLine(Iter<Character> it,
						String filename, int line) {

	var tokenStream = new ArrayList<Token>();
	var sb = new StringBuilder();

	// NOTE(Simon): These are trivially to compute if we ever encounter an
	// error. Maybe we should only compute these if we have to do it for error
	// handling?
	int startPos = 0;
	int endPos = 0;

	while (it.hasNext()) {

	    endPos += 1;
	    Character c = it.next();

	    if (c == ' ' || c == 9) { // check for tab (ASCII code 9)
		if (sb.length() != 0) {
		    tokenStream.add(new Token.Builder()
				    .filename(filename)
				    .withType(TokenType.match(sb.toString()))
				    .line(line)
				    .position(startPos, endPos)
				    .lexeme(sb.toString())
				    .build());
		    startPos = endPos;
		    sb.setLength(0); // clear StringBuilder
		} else {
		    continue; // if StringBuilder is empty we can just skip the whitespace
		}
	    } else if (c == '"') {
		startPos = endPos;
		String stringLiteral = getStringLiteral(it);
		tokenStream.add(new Token.Builder()
				.lexeme(stringLiteral)
				.filename(filename)
				.withType(TokenType.match(stringLiteral))
				.line(line)
				.build());
	    } else if (Character.isDigit(c)) {
		if (sb.length() != 0) sb.append(c);
		else {
		    startPos = endPos;
		    String strNum = Lexer.getNumLiteral(it, c);
		    tokenStream.add(new Token.Builder()
				    .lexeme(strNum)
				    .withType(TokenType.match(strNum))
				    .filename(filename)
				    .line(line)
				    .build());
		    continue;
		}
	    } else if (c == '/' && it.peek() == '/') {
		return tokenStream; // looks like we encountered a comment and can skip the rest of the line
	    } else if (Token.isSingleCharToken(c)) {
		if (sb.length() != 0) {
		    tokenStream.add(new Token.Builder()
				    .filename(filename)
				    .withType(TokenType.match(sb.toString()))
				    .line(line)
				    .position(startPos, endPos)
				    .lexeme(sb.toString())
				    .build());
		    tokenStream.add(new Token.Builder()
				    .filename(filename)
				    .withType(TokenType.match(Character.toString(c)))
				    .line(line)
				    .position(startPos, endPos)
				    .lexeme(Character.toString(c))
				    .build());
		    startPos = endPos;
		    sb.setLength(0);
		} else {
		    tokenStream.add(new Token.Builder()
				    .filename(filename)
				    .withType(TokenType.match(Character.toString(c)))
				    .line(line)
				    .position(startPos, endPos)
				    .lexeme(Character.toString(c))
				    .build());
		    startPos = endPos;
		}
	    } else {
		sb.append(c);
	    }
	}
	if (sb.length() != 0) {
	    tokenStream.add(new Token.Builder()
			    .filename(filename)
			    .withType(TokenType.match(sb.toString()))
			    .line(line)
			    .position(startPos, endPos)
			    .lexeme(sb.toString())
			    .build());
	}

	// FIXME(Simon): check if we really need to emit a semicolon
	if (tokenStream.size() > 0) {
	    //var excludeKeywords = new ArrayList<>(TokenType.IMPORT, TokenType.FUNCTION, TokenType.CLASS, TokenType.WHILE, TokenType.FOR, TokenType.UNTIL, TokenType.IF, TokenType.ELSE, TokenType.STARTBLOCK, TokenType.ENDBLOCK);

	    boolean emitSemicolon = false;
	    if (emitSemicolon) {
		tokenStream.add(new Token.Builder()
				.filename(filename)
				.line(line)
				.position(startPos + 1, endPos + 1)
				.lexeme("AUTOMATISCH EINGEFUEGT")
				.withType(TokenType.SEMICOLON)
				.build());
	    }
	}


	return tokenStream;
    }

    // FIXME(Simon): this can fail if string is initalized as empty
    /*
      a : Text = ""; //this would fail in the current implementation
    */
    public static String getStringLiteral(Iter<Character> it) {
	var sb = new StringBuilder();
	sb.append('"');
	while (it.hasNext()) {
	    var c = it.next();
	    if (c == '"') {
		sb.append(c);
		break;
	    } else {
		sb.append(c);
	    }
	}
	return sb.toString();
    }

    public static boolean emitSemicolon(List<Token> tokenStream) {
	boolean emitEOE = false;
	// for (Token t : tokenStream) {
	//     emitEOE |= endOFExprTokenNeeded(t);
	// }
	return emitEOE;
    }

    /*
      Right now numeric value can look like this:

      foo: Zahl = 12'323,23

      we only skip underscores, internally every number gets represented as a
      64bit float we can think about using some other representation for numbers
      in the future maybe DEC64 would be nice: http://www.dec64.com/
    */
    public static String getNumLiteral(Iter<Character> it, Character firstDigit) {

	var sb = new StringBuilder();
	sb.append(firstDigit);

	while (it.hasNext()) {

	    var c = it.next();

	    if (Character.isDigit(c)) {
		sb.append(c);
	    } else if (c == ',') {
		sb.append('.');
	    } else {
		it.setBackOnePosition();
		break;
	    }
	}
	return sb.toString();
    }

    public static Double parseNum(String str) { return Double.parseDouble(str); }

    public static Character[] stringToCharArray(String line) {
	var chars = new Character[line.length()];
	for (int i = 0; i < line.length(); i++) {
	    chars[i] = new Character(line.charAt(i));
	}
	return chars;
    }

    public static ArrayList<Token> tokenize(SourceFile sf) {
	var tokenStream = new ArrayList<Token>();

	while (sf.getIter().hasNext()) {
	    var chars = stringToCharArray(sf.getIter().next());
	    var it = new Iter<Character>(chars);
	    tokenStream.addAll(tokenizeLine(it, sf.getFilename(), sf.getIter().getCursor()));
	}
	// TODO(Simon): Construct the metadata of the EOF token with the right
	// values, after the last actual token from the tokenStream
	tokenStream.add(new Token(TokenType.EOF));
	return tokenStream;
    }
}

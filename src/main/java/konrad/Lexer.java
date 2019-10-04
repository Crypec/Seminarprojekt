package konrad;

import java.util.*;
import konrad.util.*;

public class Lexer {

    //TODO(Simon): refactor to Object?
    //FIXME(Simon): start and End positon don't quite work, should be getting fixed
    public static ArrayList<Token> tokenize(StringIterator it) {

	var tokenStream = new ArrayList<konrad.util.Token>();
	var sb = new StringBuilder();


	//NOTE(Simon): These are trivially to compute if we ever encounter an error. Maybe we should only compute these if we have to do it for error handling?
	//NOTE(Simon):
	int startPos = 0;
	int endPos = 0;

	while (it.hasNext()) {

	    endPos += 1;
	    char c = it.next();

	    if (c == ' ' || c == 9) { // check for tab (ASCII code 9)
		if (sb.length() != 0) {
		    tokenStream.add(new Token.Builder()
				    .filename(it.getFilename())
				    .line(it.getLine())
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
		tokenStream.add(getStringLiteral(it));
	    } else if (Character.isDigit(c)) {
		startPos = endPos;
		tokenStream.add(Lexer.getNumLiteral(it, c));
	    } else if (c == '/' && it.peek() == '/') {
		return tokenStream; // looks like we encountered a comment and can skip the rest of the line
	    } else if (Token.isSingleCharToken(c)) {
		if (sb.length() != 0) {
		    tokenStream.add(new Token.Builder()
				    .filename(it.getFilename())
				    .line(it.getLine())
				    .position(startPos, endPos)
				    .lexeme(sb.toString())
				    .build());
		    startPos = endPos;
		    sb.setLength(0);
		} else {
		    tokenStream.add(new Token.Builder()
				    .filename(it.getFilename())
				    .line(it.getLine())
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
	    var token = new Token.Builder()
		.filename(it.getFilename())
		.line(it.getLine())
		.position(startPos, endPos)
		.lexeme(sb.toString())
		.build();
	    tokenStream.add(token);
	}
	return tokenStream;
    }

    public static Token getStringLiteral(StringIterator it) {
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
	// FIXME(Simon): this can fail if string is initalized as empty
	/* 
	   a : Text = ""; //this would fail in the current implementation
	*/
	return new Token.Builder()
	    .lexeme(sb.toString())
	    .filename(it.getFilename())
	    .line(it.getLine())
	    .build();
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
    public static Token getNumLiteral(StringIterator it, Character firstDigit) {
	var sb = new StringBuilder();
	sb.append(firstDigit);

	while (it.hasNext()) {
	    var c = it.next();

	    if (Character.isDigit(c)) {
		sb.append(c);
		continue;
	    }  else if (c == ',') {
		sb.append("."); //replace Comma with dot.
		continue;
	    }  else if (c == '_') {
		continue;
	    }
	    it.setBackOnePosition();
	    break;
	}

	return new Token.Builder()
	    .lexeme(sb.toString())
	    .filename(it.getFilename())
	    .line(it.getLine())
	    .build();
    }

    public static double parseNum(String str) {
	return Double.parseDouble(str);
    }
    
    public static ArrayList<Token> tokenize(SourceFile sf) {
	var tokenStream = new ArrayList<Token>();
	while (sf.hasNext()) {
	    var sIT = new StringIterator(sf.next(), sf);
	    tokenStream.addAll(tokenize(sIT));
	}
	return tokenStream;
    }
}

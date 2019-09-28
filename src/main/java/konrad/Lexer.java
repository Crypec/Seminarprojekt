package konrad;

import java.util.ArrayList;
import konrad.util.*;

public class Lexer {

    // NOTE(Simon): this is a brutally simple version of the lexer, there is a
    // great chance we need to rewrite this NOTE(Simon): to focus on Error
    // handling... (making it compatible with taking a sourcefile)
    // FIX(Simon): this function is a pure mess, I should really fix it some day
    // => make it compatible with taking a raw sourceFile
    public static ArrayList<Token> tokenize(StringIterator it) {

	var tokenStream = new ArrayList<konrad.util.Token>();
	var sb = new StringBuilder();

	while (it.hasNext()) {
	    char c = it.next();
	    if (c == ' ' || c == 9) { // check for tab (ASCII code 9)
		if (sb.length() != 0) {
		    tokenStream.add(new Token(sb.toString()));
		    sb.setLength(0); // clear StringBuilder
		} else {
		    continue; // if StringBuilder is empty we can just skip the whitespace
		}
	    } else if (c == '"') {
		tokenStream.add(getStringLiteral(it));
	    } else if (Character.isDigit(c)) {
		var token = getNumLiteral(it, c);
		tokenStream.add(token);
	    } else if (c == '/' && it.peek() == '/') {
		// looks like we encountered a comment and can skip the rest of the line
		return tokenStream;
	    } else if (Token.isSingleCharToken(c)) {
		if (sb.length() != 0) {
		    tokenStream.add(new Token(sb.toString()));
		    sb.setLength(0);
		    tokenStream.add(new Token(Character.toString(c)));
		} else {
		    tokenStream.add(new Token(Character.toString(c)));
		}
	    } else {
		sb.append(c);
	    }
	}
	if (sb.length() != 0) {
	    tokenStream.add(new Token(sb.toString()));
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
	// FIX(Simon): this can fail if string is initalized as empty
	/*
	  a : Text = ""; //this would fail in the current implementation
	*/
	return new Token(sb.toString());
    }

    /*
      Right now numeric value can look like this:

      foo: Zahl = 123_23

      we only skip underscores, internally every number gets represented as a
      64bit float we can think about using some other representation for numbers
      in the future maybe DEC64 would be nice: http://www.dec64.com/
    */

    public static ArrayList<Token> tokenize(SourceFile sf) {
	var tokenStream = new ArrayList<Token>();
	while (sf.hasNext()) {
	    tokenStream.addAll(tokenize(new StringIterator(sf.next())));
	}
	return tokenStream;
    }


    public static Token getNumLiteral(StringIterator it, Character firstDigit) {
	var sb = new StringBuilder(firstDigit);
	sb.append(firstDigit);

	outer:
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
	Double value = parseNum(sb.toString());
	return new Token(sb.toString(), TokenType.NUMBERLITERAL, value);
    }
    public static double parseNum(String str) {
	return Double.parseDouble(str);
    }
}

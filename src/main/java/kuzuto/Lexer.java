package kuzuto;

import java.util.*;
import util.*;

public class Lexer {

    // TODO(Simon): Right now the lexer does accept all chracters as valid input -> perhaps we should do some basic error reporting right in the lexer
    public static ArrayList<Token> tokenizeLine(Iter<Character> it, String filename, int line) {

	var tokenStream = new ArrayList<Token>();
	var sb = new StringBuilder();

	//NOTE(Simon): These are trivially to compute if we ever encounter an error. Maybe we should only compute these if we have to do it for error handling?
	//FIXME(Simon): Are we shure start and end position work correctly? 
	int startPos = 0;
	int endPos = 0;

	while (it.hasNext()) {

	    endPos += 1;
	    char c = it.next();

	    if (c == ' ' || c == 9) { // check for tab (ASCII code 9)
		if (sb.length() != 0) {
		    tokenStream.add(new Token.Builder()
				    .filename(filename)
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
		tokenStream.add(new Token.Builder()
				.lexeme(getStringLiteral(it))
				.filename(filename)
				.line(line)
				.build());
	    } else if (Character.isDigit(c)) {
		if (sb.length() != 0) sb.append(c);
		else {
		    startPos = endPos;  
		    String strNum = Lexer.getNumLiteral(it, c);
		    tokenStream.add(new Token.Builder()
				    .lexeme(strNum)
				    .filename(filename)
				    .line(line)
				    .build());
		} 
	    } else if (c == '/' && it.peek() == '/') {
		return tokenStream; // looks like we encountered a comment and can skip the rest of the line
	    } else if (Token.isSingleCharToken(c)) {
		if (sb.length() != 0) {
		    tokenStream.add(new Token.Builder()
				    .filename(filename)
				    .line(line)
				    .position(startPos, endPos)
				    .lexeme(sb.toString())
				    .build());
		    tokenStream.add(new Token.Builder()
				    .filename(filename)
				    .line(line)
				    .position(startPos, endPos)
				    .lexeme(Character.toString(c))
				    .build());
		    startPos = endPos;
		    sb.setLength(0);
		} else {
		    tokenStream.add(new Token.Builder()
				    .filename(filename)
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
	    var token = new Token.Builder()
		.filename(filename)
		.line(line)
		.position(startPos, endPos)
		.lexeme(sb.toString())
		.build();
	    tokenStream.add(token);
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
	    System.out.println(c);
	    if (Character.isDigit(c)) {
		sb.append(c);
		continue;
	    }  else if (c == ',') {
		sb.append("."); //replace Comma with dot.
		continue;
	    }  else if (c == '_') {
		continue;
	    } else {
		break;
	    }
	}
	it.setBackOnePosition(); 
	return sb.toString();
    }

    public static double parseNum(String str) {
	return Double.parseDouble(str);
    }

    public static Character[] stringToCharArray(String line) {
	var chars = new Character[line.length()];
	for (int i = 0; i < line.length(); i++) {
	    chars[i] = new Character(line.charAt(i));
	}
	return chars;
    }
    
    public static ArrayList<Token> tokenize(SourceFile sf) {
	var tokenStream = new ArrayList<Token>();
	while (sf.hasNext()) {
	    var chars = stringToCharArray(sf.next());
	    var it = new Iter<Character>(chars);
	    tokenStream.addAll(tokenizeLine(it, sf.getFilename(), sf.getLine()));
	}
	// TODO(Simon): Construct the metadata of the EOF token with the right values, after the last actual token from the tokenStream
	tokenStream.add(new Token(TokenType.EOF));
	return tokenStream;
    }
}

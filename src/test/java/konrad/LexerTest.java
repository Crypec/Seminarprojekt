package konrad;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.*;


import konrad.*;
import konrad.util.*;


public class LexerTest {

    @Test
    public void testTokenizeFunctionDecl() {

	String testDecl = "fun bar(x: Zahl, y: Zahl) -> Text";

	var tokenStream = Lexer.tokenize(new StringIterator(testDecl));

	var actual = new ArrayList<Token>();
	actual.add(new Token("fun"));
	actual.add(new Token("bar"));
	actual.add(new Token("("));
	actual.add(new Token("x"));
	actual.add(new Token(":"));
	actual.add(new Token("Zahl"));
	actual.add(new Token(","));
	actual.add(new Token("y"));
	actual.add(new Token(":"));
	actual.add(new Token("Zahl"));
	actual.add(new Token(")"));
	actual.add(new Token("->"));
	actual.add(new Token("Text"));

	assertTrue(listEquals(tokenStream, actual));
    }
    @Test
    public void testTokenizeVarDef() {

	String testDecl = "foo: Text = \"Hello World\"";
	var tokenStream = Lexer.tokenize(new StringIterator(testDecl));

	var actual = new ArrayList<Token>();

	actual.add(new Token("foo"));
	actual.add(new Token(":"));
	actual.add(new Token("Text"));
	actual.add(new Token("="));
	actual.add(new Token("\"Hello World\""));

	assertTrue(listEquals(tokenStream, actual));
    }

    @Test
    public void testTokenizeWhileDecl() {
	String testDecl = "solange(x != 0) {}";

	var tokenStream = Lexer.tokenize(new StringIterator(testDecl));

	var actual = new ArrayList<Token>();

	actual.add(new Token("solange"));
	actual.add(new Token("("));
	actual.add(new Token("x"));
	actual.add(new Token("!="));
	actual.add(new Token("0", TokenType.NUMBERLITERAL, 0));
	actual.add(new Token(")"));
	actual.add(new Token("{"));
	actual.add(new Token("}"));

	assertTrue(listEquals(tokenStream, actual));
    }

    public static boolean listEquals(ArrayList<Token> given, ArrayList<Token> wanted) {

	if (given.size() != wanted.size()) return false;

	for (int i = 0; i < wanted.size(); i++) {
	    if (given.get(i).getType() != wanted.get(i).getType()) return false;
	}
	return true;
   }
}

package konrad;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.*;


import konrad.*;
import konrad.util.*;
import konrad.util.common.*;


public class LexerTest {

    @Test
    public void testTokenizeFunctionDecl() {

	String testDecl = "definiere funktion bar(x: Zahl, y: Zahl) -> Text";

	var tokenStream = Lexer.tokenize(new StringIterator(testDecl));

	var actual = new ArrayList<Token>();
	actual.add(new Token("definiere"));
	actual.add(new Token("funktion"));
	actual.add(new Token("test"));
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

	//assertTrue(tokenStream.equals(actual));
    }
    @Test
    public void testTokenizeVarDef() {

	String testDecl = "foo: Text = \"Hello World\"";
	var tokenStream = Lexer.tokenize(new StringIterator(testDecl));

	var actual = new ArrayList<Token>();

	actual.add(new Token("foo"));
	    actual.add(new Token(":"));
	    actual.add(new Token("text"));
	    actual.add(new Token("="));
	    actual.add(new Token("\"Hello World\""));

	    //assertTrue(tokenStream.equals(actual));
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

	//assertTrue(tokenStream.equals(actual));
    }
}

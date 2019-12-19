package core;

import com.google.common.collect.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;
import util.*;

public class LexerTest {

    @Test
    public void lexFunctionDecl() {

	String testcase = "fun test(x: Zahl, y: Text) -> Zahl {}";

	var charArr = testcase
	    .chars()
	    .mapToObj(c -> (char) c)
	    .toArray(Character[]::new);
	var actual = Lexer.tokenizeLine(new Iter(charArr), "funcionDeclTest", 0); // TODO(Simon): replace with real scanner

	var expected = new ArrayList() {{
	    add(TokenType.FUNCTION);
	    add(TokenType.SYMBOL);
	    add(TokenType.LPAREN);
	    add(TokenType.SYMBOL);
	    add(TokenType.COLON);
	    add(TokenType.SYMBOL);
	    add(TokenType.COMMA);
	    add(TokenType.SYMBOL);
	    add(TokenType.COLON);
	    add(TokenType.SYMBOL);
	    add(TokenType.RPAREN);
	    add(TokenType.ARROW);
	    add(TokenType.SYMBOL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.ENDBLOCK);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexWhileLoop() {

	String testcase = "solange foo > 10 {}";

	var charArr = testcase
	    .chars()
	    .mapToObj(c -> (char) c)
	    .toArray(Character[]::new);
	var actual = Lexer.tokenizeLine(new Iter(charArr), "funcionDeclTest", 0); // TODO(Simon): replace with real scanner

	var expected = new ArrayList() {{
	    add(TokenType.WHILE);
	    add(TokenType.SYMBOL);
	    add(TokenType.GREATER);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.ENDBLOCK);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexeImport() {

	String testcase = """
	    #benutze [
		"Basis",
		"Fmt",
		      ]""";

	var charArr = testcase
	    .chars()
	    .mapToObj(c -> (char) c)
	    .toArray(Character[]::new);
	var actual = Lexer.tokenizeLine(new Iter(charArr), "funcionDeclTest", 0); // TODO(Simon): replace with real scanner

	var expected = new ArrayList() {{
	    add(TokenType.IMPORT);
	    add(TokenType.LBRACKET);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.COMMA);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.COMMA);
	    add(TokenType.RBRACKET);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexExampleFunction() {

	String testcase = """
	    fun test(x: Bool) -> Text {
	    wenn x {
		rueckgabe "Hello World";
	    } sonst wenn wahr {
		a := 20;
	    }

	    solange a != 10 {
		a = #eingabe("Gib eine Zahl ein")
		wenn a == 10 {
		    #ausgabe("{} ist die falsche Zahl", a)
		}
	    }
	    rueckgabe "FOO BAR"
	}
	]""";

    var charArr = testcase
	.chars()
	.mapToObj(c -> (char) c)
	.toArray(Character[]::new);
	var actual = Lexer.tokenizeLine(new Iter(charArr), "funcionDeclTest", 0); // TODO(Simon): replace with real scanner

	var expected = new ArrayList() {{
	    add(TokenType.FUNCTION);
	    add(TokenType.SYMBOL);
	    add(TokenType.LPAREN);
	    add(TokenType.SYMBOL);
	    add(TokenType.COLON);
	    add(TokenType.SYMBOL);
	    add(TokenType.RPAREN);
	    add(TokenType.ARROW);
	    add(TokenType.SYMBOL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.IF);
	    add(TokenType.SYMBOL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.RETURN);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.SEMICOLON);
	    add(TokenType.ENDBLOCK);
	    add(TokenType.ELSE);
	    add(TokenType.IF);
	    add(TokenType.TRUE);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.SYMBOL);
	    add(TokenType.VARDEF);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.SEMICOLON);
	    add(TokenType.ENDBLOCK);
	    add(TokenType.WHILE);
	    add(TokenType.SYMBOL);
	    add(TokenType.NOTEQUAL);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.SYMBOL);
	    add(TokenType.EQUALSIGN);
	    add(TokenType.READINPUT);
	    add(TokenType.LPAREN);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.LPAREN);
	    add(TokenType.IF);
	    add(TokenType.SYMBOL);
	    add(TokenType.EQUALEQUAL);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.PRINT);
	    add(TokenType.RPAREN);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.COMMA);
	    add(TokenType.SYMBOL);
	    add(TokenType.RPAREN);
	    add(TokenType.ENDBLOCK);
	    add(TokenType.ENDBLOCK);
	    add(TokenType.RETURN);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.ENDBLOCK);
	}};
    assertListEqual(actual, expected);
    }


    @Test
    public void lexVarDef() {

	String testcase = """
	    foo := "Hello" + "World"
	    """;

	var charArr = testcase
	    .chars()
	    .mapToObj(c -> (char) c)
	    .toArray(Character[]::new);
	var actual = Lexer.tokenizeLine(new Iter(charArr), "funcionDeclTest", 0); // TODO(Simon): replace with real scanner

	var expected = new ArrayList() {{
	    add(TokenType.SYMBOL);
	    add(TokenType.VARDEF);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.PLUS);
	    add(TokenType.STRINGLITERAL);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexTypeDecl() {

	String testcase = """
	    Typ: Adress {
	    FirstName: Text,
	    LastName: Text,
	    Street: Text,
	    HouseNumer: Zahl,
	}
	""";

	var charArr = testcase
	    .chars()
	    .mapToObj(c -> (char) c)
	    .toArray(Character[]::new);
	var actual = Lexer.tokenizeLine(new Iter(charArr), "funcionDeclTest", 0); // TODO(Simon): replace with real scanner

	var expected = new ArrayList() {{
	    add(TokenType.CLASS);
	    add(TokenType.COLON);
	    add(TokenType.SYMBOL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.SYMBOL);
	    add(TokenType.COLON);
	    add(TokenType.SYMBOL);
	    add(TokenType.COMMA);
	    add(TokenType.SYMBOL);
	    add(TokenType.COLON);
	    add(TokenType.SYMBOL);
	    add(TokenType.COMMA);
	    add(TokenType.SYMBOL);
	    add(TokenType.COLON);
	    add(TokenType.SYMBOL);
	    add(TokenType.COMMA);
	    add(TokenType.SYMBOL);
	    add(TokenType.COLON);
	    add(TokenType.SYMBOL);
	    add(TokenType.COMMA);
	    add(TokenType.STARTBLOCK);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexAssingment() {

	String testcase = "foo = (a + 3)*3";

	var charArr = testcase
	    .chars()
	    .mapToObj(c -> (char) c)
	    .toArray(Character[]::new);
	var actual = Lexer.tokenizeLine(new Iter(charArr), "funcionDeclTest", 0); // TODO(Simon): replace with real scanner

	var expected = new ArrayList() {{
	    add(TokenType.SYMBOL);
	    add(TokenType.EQUALSIGN);
	    add(TokenType.LPAREN);
	    add(TokenType.SYMBOL);
	    add(TokenType.PLUS);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.RPAREN);
	    add(TokenType.MULTIPLY);
	    add(TokenType.NUMBERLITERAL);
	}};
	assertListEqual(actual, expected);
    }
    @Test
    public void lexForLoop() {

	String testcase = "fuer i := 0..10 {}";

	var charArr = testcase
	    .chars()
	    .mapToObj(c -> (char) c)
	    .toArray(Character[]::new);
	var actual = Lexer.tokenizeLine(new Iter(charArr), "funcionDeclTest", 0); // TODO(Simon): replace with real scanner

	var expected = new ArrayList() {{
	    add(TokenType.FOR);
	    add(TokenType.SYMBOL);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.UNTIL);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.ENDBLOCK);
	}};
	assertListEqual(actual, expected);
    }

    public static void assertListEqual(List<Token> expected, List<TokenType> actual) {
	assertTrue("length of expected != length of actual", expected.size() == actual.size());
	var res = Streams.zip(expected.stream(), actual.stream(), (e, a) -> e.getType() != a)
	    .anyMatch(x -> x);
	assertFalse("expected TokenTypes != actual TokenTypes", res);
    }
}

package core;

import com.google.common.collect.*;
import java.util.*;
import core.*;
import static org.junit.Assert.*;
import org.junit.*;
import util.*;

public class Lexing {

    @Test
    public void lexFunctionDecl() {

	String testcase = "fun test(x: Zahl, y: Text) -> Zahl {}";
	var actual = new Lexer(testcase, "LEX_TEST_FUNCTIONDECL").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.FUNCTION);
	    add(TokenType.IDEN);
	    add(TokenType.LPAREN);
	    add(TokenType.IDEN);
	    add(TokenType.COLON);
	    add(TokenType.IDEN);
	    add(TokenType.COMMA);
	    add(TokenType.IDEN);
	    add(TokenType.COLON);
	    add(TokenType.IDEN);
	    add(TokenType.RPAREN);
	    add(TokenType.ARROW);
	    add(TokenType.IDEN);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.ENDBLOCK);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexMathExpr() {

	String testcase = "a / (-2*3+(x))";

	var actual = new Lexer(testcase, "LEX_TEST_MATH_EXPR").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.IDEN);
	    add(TokenType.DIVIDE);
	    add(TokenType.LPAREN);
	    add(TokenType.MINUS);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.MULTIPLY);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.PLUS);
	    add(TokenType.LPAREN);
	    add(TokenType.IDEN);
	    add(TokenType.RPAREN);
	    add(TokenType.RPAREN);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexStringConcatExpr() {

	String testcase = """
	    "Hello" + "WORLD"
	    """;

	var actual = new Lexer(testcase, "LEX_TEST_MATH_EXPR").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.PLUS);
	    add(TokenType.STRINGLITERAL);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexNullAssignment() {

	String testcase = "foo = #Null;";

	var actual = new Lexer(testcase, "LEX_TEST_NULL_ASSIGNMENT").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.IDEN);
	    add(TokenType.EQUALSIGN);
	    add(TokenType.NULL);
	    add(TokenType.SEMICOLON);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexBoolExpr() {

	String testcase = "((!x und y) oder (x und !y))";

	var actual = new Lexer(testcase, "LEX_TEST_BOOL_EXPR").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.LPAREN);
	    add(TokenType.LPAREN);
	    add(TokenType.NOT);
	    add(TokenType.IDEN);
	    add(TokenType.AND);
	    add(TokenType.IDEN);
	    add(TokenType.RPAREN);
	    add(TokenType.OR);
	    add(TokenType.LPAREN);
	    add(TokenType.IDEN);
	    add(TokenType.AND);
	    add(TokenType.NOT);
	    add(TokenType.IDEN);
	    add(TokenType.RPAREN);
	    add(TokenType.RPAREN);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexWhileLoop() {

	String testcase = "solange foo > 10 {}";
	var actual = new Lexer(testcase, "LEX_TEST_WHILE_LOOP").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.WHILE);
	    add(TokenType.IDEN);
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

	var actual = new Lexer(testcase, "LEX_IMPORT").tokenize();

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
	""";
	var actual = new Lexer(testcase, "LEX_TEST_EXAMPLE_FUNCTION").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.FUNCTION);
	    add(TokenType.IDEN);
	    add(TokenType.LPAREN);
	    add(TokenType.IDEN);
	    add(TokenType.COLON);
	    add(TokenType.IDEN);
	    add(TokenType.RPAREN);
	    add(TokenType.ARROW);
	    add(TokenType.IDEN);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.IF);
	    add(TokenType.IDEN);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.RETURN);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.SEMICOLON);
	    add(TokenType.ENDBLOCK);
	    add(TokenType.ELSE);
	    add(TokenType.IF);
	    add(TokenType.TRUE);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.IDEN);
	    add(TokenType.VARDEF);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.SEMICOLON);
	    add(TokenType.ENDBLOCK);
	    add(TokenType.WHILE);
	    add(TokenType.IDEN);
	    add(TokenType.NOTEQUAL);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.IDEN);
	    add(TokenType.EQUALSIGN);
	    add(TokenType.READINPUT);
	    add(TokenType.LPAREN);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.RPAREN);
	    add(TokenType.IF);
	    add(TokenType.IDEN);
	    add(TokenType.EQUALEQUAL);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.PRINT);
	    add(TokenType.LPAREN);
	    add(TokenType.STRINGLITERAL);
	    add(TokenType.COMMA);
	    add(TokenType.IDEN);
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
	var actual = new Lexer(testcase, "LEX_TEST_VAR_DEF").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.IDEN);
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
	var actual = new Lexer(testcase, "LEX_TEST_TYPEDECL").tokenize();


	var expected = new ArrayList() {{
	    add(TokenType.CLASS);
	    add(TokenType.COLON);
	    add(TokenType.IDEN);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.IDEN);
	    add(TokenType.COLON);
	    add(TokenType.IDEN);
	    add(TokenType.COMMA);
	    add(TokenType.IDEN);
	    add(TokenType.COLON);
	    add(TokenType.IDEN);
	    add(TokenType.COMMA);
	    add(TokenType.IDEN);
	    add(TokenType.COLON);
	    add(TokenType.IDEN);
	    add(TokenType.COMMA);
	    add(TokenType.IDEN);
	    add(TokenType.COLON);
	    add(TokenType.IDEN);
	    add(TokenType.COMMA);
	    add(TokenType.ENDBLOCK);
	}};
	assertListEqual(actual, expected);
    }

    @Test
    public void lexAssingment() {

	String testcase = "foo = (a + 3)*3";
	var actual = new Lexer(testcase, "LEX_TEST_ASSIGNMENT").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.IDEN);
	    add(TokenType.EQUALSIGN);
	    add(TokenType.LPAREN);
	    add(TokenType.IDEN);
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

	String testcase = "fuer i := 0 bis 10 {}";

	var actual = new Lexer(testcase, "LEX_TEST_FOR_LOOP").tokenize();

	var expected = new ArrayList() {{
	    add(TokenType.FOR);
	    add(TokenType.IDEN);
	    add(TokenType.VARDEF);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.UNTIL);
	    add(TokenType.NUMBERLITERAL);
	    add(TokenType.STARTBLOCK);
	    add(TokenType.ENDBLOCK);
	}};
	assertListEqual(actual, expected);
    }

    public static void assertListEqual(List<Token> actual, List<TokenType> expected) {
	assertTrue("length of expected != length of actual", expected.size() == actual.size());
	var res = Streams.zip(expected.stream(), actual.stream(), (a, e) -> e.getType() != a)
	    .anyMatch(x -> x);
	assertFalse("expected TokenTypes != actual TokenTypes", res);
    }
    public static void diffPrintList(List<Token> actual, List<TokenType> expected) {
	for (int i = 0; i < actual.size(); i++) {
	    if (actual.get(i).getType() != expected.get(i)) {
		System.out.printf("[DIFF] :: actual token with type %s at index %d, does not mactch expected type %s %n", actual.get(i).getType(), i, expected.get(i));
	    }
	}
    }
}

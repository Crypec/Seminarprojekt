package core;

import static org.junit.Assert.*;

import java.util.*;
import org.junit.*;
import util.*;

public class Parsing {

    // @Test
    // public void parseVoidFunctionDecl() {

    // String testcase = "fun test(x: Zahl, y: Text) {}";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_FUNCTIONDECL_VOID").tokenize();

    // var functionDecl = new Parser(tokenStream.toArray(Token[]::new)).parseFunctionDecl(false);

    // assertEquals(functionDecl.getName(), tokenStream.get(1));
    // assertEquals(functionDecl.getParameters().get(0).getName(), tokenStream.get(3)); //
    // parameter: x
    // assertEquals(functionDecl.getParameters().get(0).getType(), tokenStream.get(5)); // type:
    // Zahl
    // assertEquals(functionDecl.getParameters().get(1).getName(), tokenStream.get(7)); //
    // parameter: y
    // assertEquals(functionDecl.getParameters().get(1).getType(), tokenStream.get(9)); // type:
    // Zahl
    // assertEquals(functionDecl.getReturnType(), null);
    // assertTrue(functionDecl.getBody().getStatements().size() == 0);
    // }

    // @Test
    // public void parseFunctionDeclWithReturn() {

    // String testcase = "fun test(x: Zahl, y: Text) -> Zahl {}";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_FUNCTIONDECL_WITH_RETURN").tokenize();

    // var functionDecl = new Parser(new
    // Iter(tokenStream.toArray(Token[]::new))).parseFunctionDecl(false);

    // assertEquals(functionDecl.getName(), tokenStream.get(1));
    // assertEquals(functionDecl.getParameters().get(0).getName(), tokenStream.get(3)); //
    // parameter: x
    // assertEquals(functionDecl.getParameters().get(0).getType(), tokenStream.get(5)); // type:
    // Zahl
    // assertEquals(functionDecl.getParameters().get(1).getName(), tokenStream.get(7)); //
    // parameter: y
    // assertEquals(functionDecl.getParameters().get(1).getType(), tokenStream.get(9)); // type:
    // Zahl
    // assertEquals(functionDecl.getReturnType(), tokenStream.get(12)); // returnType: Zahl
    // assertTrue(functionDecl.getBody().getStatements().size() == 0);
    // }

    // @Test
    // public void parseFunctionDeclNoParams() {

    // String testcase = "fun test() -> Zahl {}";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_FUNCTIONDECL_NO_PARAMS").tokenize();

    // var functionDecl = new Parser(new
    // Iter(tokenStream.toArray(Token[]::new))).parseFunctionDecl(false);

    // assertEquals(functionDecl.getName(), tokenStream.get(1));
    // assertEquals(functionDecl.getReturnType(), tokenStream.get(5));
    // assertTrue(functionDecl.getBody().getStatements().size() == 0);
    // }

    // @Test
    // public void parseBreak() {

    // String testcase = "stopp;";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_BREAK").tokenize();

    // var breakNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseBreak();
    // assertEquals(breakNode.getLocation(), tokenStream.get(0));
    // }

    // @Test
    // public void parseAssingment() {

    // String testcase = "foo = 2;";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_ASSIGNMENT").tokenize();

    // var assignmentNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExprStmt();

    // var name = ((Expr.Assign)assignmentNode.getExpression()).getName(); // No it's fine, no
    // reason to hate java!?!
    // assertEquals(name, tokenStream.get(0));
    // }

    // @Test
    // public void parsePrint() {

    // 	String testcase = """
    // 		#ausgabe("Hello {}", "World");
    // 	""";
    // 	var tokenStream = new Lexer(testcase, "PARSE_TEST_PRINT").tokenize();

    // var printNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parsePrint();
    // assertEquals(printNode.getFormatter(), tokenStream.get(2));
    // assertTrue(printNode.getExpressions().size() == 1);
    // }

    // @Test
    // public void parseReturn() {

    // String testcase = "rueckgabe 3;";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_RETURN").tokenize();

    // var returnNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseReturn();
    // assertEquals(returnNode.getLocation(), tokenStream.get(0));
    // }

    // @Test
    // public void parseImport() {

    // 	String testcase = """
    // 		#benutze [
    // 				  "Mathe",
    // 				  "Fmt",
    // 				  ]
    // 		""";
    // 	var tokenStream = new Lexer(testcase, "PARSE_TEST_IMPORT").tokenize();

    // 	var importNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseImport();
    // assertEquals(importNode.getLibs().get(0), tokenStream.get(2));
    // assertEquals(importNode.getLibs().get(1), tokenStream.get(4));
    // }

    // @Test
    // public void parseStructNormal() {

    // 	String testcase = """
    // 		Typ: Foo {
    // 		bar: Bar,
    // 		name: Text,
    // 	}
    // """;
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_STRUCT_NORMAL").tokenize();

    // var structDecl = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseStructDecl();
    // assertEquals(structDecl.getName(), tokenStream.get(2)); // name: foo
    // assertEquals(structDecl.getMembers().get(0).getName(), tokenStream.get(4)); // member: bar
    // assertEquals(structDecl.getMembers().get(0).getType(), tokenStream.get(6)); // type of bar:
    // Bar
    // assertEquals(structDecl.getMembers().get(1).getName(), tokenStream.get(8)); // member: name
    // assertEquals(structDecl.getMembers().get(1).getType(), tokenStream.get(10)); // type of name:
    // Text
    // }

    // @Test
    // public void parseStructEmpty() {

    // String testcase = """
    //     Typ: Test {
    // }
    // """;
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_STRUCT_Empty").tokenize();

    // var structDecl = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseStructDecl();
    // assertEquals(structDecl.getName(), tokenStream.get(2)); // name: foo
    // }

    // @Test
    // public void parseVarDefUntyped() {

    // String testcase = "x := 3;";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_VARDEF_UNTYPED").tokenize();

    // var varDef = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseVarDef();
    // assertEquals(varDef.getName(), tokenStream.get(0)); // name: x
    // }

    // @Test
    // public void parseVarDefTyped() {

    // String testcase = "x: Zahl = 3;";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_VARDEF_TYPED").tokenize();

    // var varDef = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseVarDef();
    // assertEquals(varDef.getName(), tokenStream.get(0)); // name: x
    // assertEquals(varDef.getType(), tokenStream.get(2)); // type: Zahl
    // }

    // @Test
    // public void parseWhileLoop() {

    // String testcase = "solange 10 != 0 {}";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_WHILE_LOOP").tokenize();

    // var loopDecl = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseWhileLoop();
    // assertTrue(loopDecl.getCondition() != null);
    // assertTrue(loopDecl.getBody().getStatements().size() == 0);
    // }

    // @Test
    // public void parseNormalForLoop() {

    // String testcase = "fuer i := 0 bis 10 {}";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_NORMAL_FOR_LOOP").tokenize();

    // var loopDecl = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseForLoop();
    // assertTrue(loopDecl.getCondition() != null);
    // assertTrue(loopDecl.getBody().getStatements().size() == 1); // check that assignment of loop
    // variable gets inserted into the body
    // }

    // @Test
    // public void parsePlusExpr() {

    // String testcase = "42 + 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_PLUS_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.PLUS);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseMinusExpr() {

    // String testcase = "42 - 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_MINUS_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.MINUS);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseMultiplyExpr() {

    // String testcase = "42 * 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_MULTIPLY_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.MULTIPLY);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseDivideExpr() {

    // String testcase = "42 / 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_DIVIDE_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.DIVIDE);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseModuloExpr() {

    // String testcase = "42 % 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_MODULO_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.MODULO);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseEqualExpr() {

    // String testcase = "42 == 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_EQUAL_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.EQUALEQUAL);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseNotEqualExpr() {

    // String testcase = "42 != 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_NOT_EQUAL_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.NOTEQUAL);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseLessThaExpr() {

    // String testcase = "42 < 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_LESS_THAN_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.LESS);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseGreaterThanExpr() {

    // String testcase = "42 > 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_GREATER_THAN_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.GREATER);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseGreaterOrEqual() {

    // String testcase = "42 >= 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_GREATER_OR_EQUAL_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.GREATEREQUAL);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }

    // @Test
    // public void parseLessOrEqual() {

    // String testcase = "42 <= 32";
    // var tokenStream = new Lexer(testcase, "PARSE_TEST_LESS_OR_EQUAL_EXPR").tokenize();

    // var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parseExpr();
    // var binaryNode = (Expr.Binary)ASTNode;

    // var leftLiteralNode = (Expr.Literal)binaryNode.getLeft();
    // var rightLiteralNode = (Expr.Literal)binaryNode.getRight();
    // assertEquals(binaryNode.getOperator().getType(), TokenType.LESSEQUAL);
    // assertEquals(leftLiteralNode.getValue(), 42.0);
    // assertEquals(rightLiteralNode.getValue(), 32.0);
    // }
}

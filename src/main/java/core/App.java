/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package core;

import util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {

    public static void main(String... args) throws IOException {

	String path = "./examples/example.zt";
	String source = new String(Files.readAllBytes(Paths.get(path)));

	var tokenStream = new Lexer(source, path).tokenize();
	var ASTNode = new Parser(new Iter(tokenStream.toArray(Token[]::new))).parse();
	System.out.println(tokenStream);
    }
}

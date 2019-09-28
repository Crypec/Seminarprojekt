package konrad;

import java.util.*;
import konrad.util.*;

public class Parser {

    public static ASTNode parseFunctionDecl(Iterator<Token> it) {

	var funcDecl = new ASTNode.FunctionDecl();

	while (it.hasNext()) {
	    Token t = it.next();
	    if (t.getType() == TokenType.STARTBLOCK) {
		//handle block
	    }
	}
	return new ASTNode.FunctionDecl();
    }
}

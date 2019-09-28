package konrad.util;

import java.util.*;

public abstract class ASTNode {

    private ArrayList<ASTNode> children;
    
    public static class FunctionDecl extends ASTNode {

	public String name;
	public ArrayList<Argument> args;
	public TokenType returnType;  

	public FunctionDecl(String name, ArrayList<Argument> args, TokenType returnType) {
	    this.name = name;
	    // TODO(Simon): handle max count of args, we should probably restrict the max number of argument, just to force the user to write cleaner code 
	    this.args = args;
	    this.returnType = returnType;
	}

	public FunctionDecl() {}

	public FunctionDecl(Iterator<Token> it) {

	    this.name = it.next().getLexeme();

	    while (it.hasNext()) {
		Token current = it.next();
		if (current.getType() == TokenType.LPAREN) break;

		if (current.getType() == TokenType.SYMBOL) {
		    if (!it.hasNext()) {
		    }
		}
	    }
	    super.children.add(new ASTNode.Block(it));
	}
    }

    public static class Block extends ASTNode {
	public Block(Iterator<Token> it) {
	    
	}
    }
}

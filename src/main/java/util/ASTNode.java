package konrad.util;

import java.util.*;
import konrad.util.TokenType;
public abstract class ASTNode {

    private ArrayList<ASTNode> children;
    
    public static class FunctionDecl extends ASTNode {

	private String name;
	private ArrayList<Argument> args;
	private TokenType returnType;  

	public FunctionDecl(String name, ArrayList<Argument> args, TokenType returnType) {
	    this.name = name;
	    // TODO(Simon): handle max count of args, we should probably restrict the max number of argument, just to force the user to write cleaner code 
	    this.args = args;
	    this.returnType = returnType;
	}

	public FunctionDecl() {}

	public FunctionDecl(Iterator<Token> it) {
	    while (it.hasNext()) {
		Token current = it.next();
		if (current = STARTBLOCK) {
		    this.left = new Block();
		}
	    }
	
	}
	
	public void setName(String name) {
	    this.name = name;
	}

	public void setArgs(ArrayList<Argument> args) {
	    this.args = args;
	}

	public void setRetType(TokenType type) {
	    this.returnType = type;
	}
	
	public String getName() {
	    return this.name;
	}
	public ArrayList<Argument> getArgs() {
	    return this.args;
	}
	public TokenType getRetType() {
	    return this.returnType;
	}


    }
 }

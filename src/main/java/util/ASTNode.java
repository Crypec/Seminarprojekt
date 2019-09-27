package konrad.util;

public abstract class ASTNode {

    private ASTNode left;
    private ASTNode right;
    
    public static class FunktionsKopf extends ASTNode {

	private String name;
	private ArrayList<Argument> argumente;
	private TokenType returnType;  

	public FunktionsKopf(String name, ArrayList<Argumente> args, TokenType returnType) {
	    this.name = name;
	    this.argumente = args;
	    this.returnType = returnType;
	}
	
    }

    
}

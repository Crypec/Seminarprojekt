package konrad.util;

public class ASTNode {

    private ASTNode left;
    private ASTNode right;

    private NodeType type;
    public Object value;

    public ASTNode (NodeType type, Object value,  ASTNode right, ASTNode left) {
	this.right = right;
	this.left = left;
	this.value = value;
	this.type = type;
    }

    public ASTNode getLeft() {
	return this.left;
    }
    
    public ASTNode getRight() {
	return this.right;
    }
    
    
}

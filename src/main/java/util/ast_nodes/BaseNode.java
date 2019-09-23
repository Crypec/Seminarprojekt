package konrad.util.ast_nodes;

public abstract class BaseNode {
    
    private BaseNode leftChild;
    private BaseNode rightChild;

    public BaseNode(BaseNode left, BaseNode right) {
	this.leftChild = left;
	this.rightChild = right;
    }
    
}

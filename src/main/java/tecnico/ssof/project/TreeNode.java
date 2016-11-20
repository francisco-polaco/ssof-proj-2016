package tecnico.ssof.project;

import java.util.LinkedList;
import java.util.List;

public class TreeNode {

	public String getText() {
		return text;
	}

	public TreeNode getParent() {
		return parent;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	private String text;
	private int line;
	private TreeNode parent;
	private List<TreeNode> children;
	
	public TreeNode(String text, int line) {
		
		this.text = text;
		this.line = line;
		this.children = new LinkedList<TreeNode>();
	}
	
	/// Adds a child to this node
	///
	/// @return: the new child 
	public TreeNode addChild(String text, int line) {

		TreeNode childNode = new TreeNode(text,line);
		childNode.parent = this;
		this.children.add(childNode);
		
		return childNode;
	}
	
	/// Gets a child from this node
	///
	/// @ return: child at given index
	public TreeNode getChildAt(int index) throws IndexOutOfBoundsException {

		try{

			TreeNode childToReturn = this.children.get(index);

		}catch(IndexOutOfBoundsException exception){
			System.err.println("Not a valid index");
		}

	public TreeNode getChildAt(int index) {
		
		// TODO
		return children.get(index);
	}
	
	/// Total number of children
	///
	/// @return: number of children
	public int getChildCount() {
		
		return this.children.size();

	}
	
	/// Get line
	///
	/// @return: line number of this node
	public int getLine() {
		
		return line;
	}
	
	/// Check if node is a leaf
	///
	/// @return: true if it is a leaf; false otherwise
	public boolean isLeaf() {
		
		if(this.getChildCount() == 0){
			return true;
		}
		else
			return false;
	}
	
	/// Prints tree
	public void print() {
		while()
	}
}

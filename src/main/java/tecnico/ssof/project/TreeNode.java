package tecnico.ssof.project;

import java.util.LinkedList;
import java.util.List;

public class TreeNode {

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
		
		// TODO
//		public TreeNode<T> addChild(T child) {
//	        TreeNode<T> childNode = new TreeNode<T>(child, line);
//	        childNode.parent = this;
//	        this.children.add(childNode);
//	        return childNode;
//	    }
		return null;
	}
	
	/// Gets a child from this node
	///
	/// @ return: child at given index
	public TreeNode getChildAt(int index) {
		
		// TODO
		return null;
	}
	
	/// Total number of children
	///
	/// @return: number of children
	public int getChildCount() {
		
		// TODO
		return 0;
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
		
		// TODO
		return false;
	}
}

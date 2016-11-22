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
	
	public int getLine() {
		
		return line;
	}
		
	public String getText() {
		return text;
	}

	public TreeNode getParent() {
		return parent;
	}

	public List<TreeNode> getChildren() {
		return children;
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

		if(this.getChildCount() > index){
			TreeNode childToReturn = this.children.get(index);
			return childToReturn;
		}
		else{
			System.err.println("Not a valid index");
			throw new IndexOutOfBoundsException();
		}
	}
	
	/// Total number of children
	///
	/// @return: number of children
	public int getChildCount() {
		
		return this.children.size();

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
//	public void print() {
//		
//		System.out.println("\nTreeNode.print()");
//		TreeNode root = this;
//		TreeNode actual = this.getChildAt(0);
//		int i;
//		while(actual != root){
//			if(actual.isLeaf()){
//				System.out.println(actual.getText());
//				i = actual.parent.getChildren().indexOf(actual) + 1;
//				if(i > actual.parent.getChildCount()){
//					if(actual.parent == root){
//						return;
//					}
//					while(actual.parent.parent.getChildCount() < 2){
//						System.out.println(actual.parent.getText());
//						actual = actual.parent;
//					}
//					int next = actual.parent.getChildren().indexOf(actual.parent) + 1;
//					actual = actual.parent.parent.getChildAt(next);
//				}
//				else{
//					actual = actual.parent.getChildAt(i);
//				}
//			}
//			else{
//				actual = actual.getChildAt(0);
//			}
//
//		}
//	}
	
	public static void print(TreeNode parent) {
		
		for(TreeNode node: parent.getChildren())
		{
			System.out.println(node.getText());
			if(!node.isLeaf())
				print(node);
		}
	}
}

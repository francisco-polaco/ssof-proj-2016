package tecnico.ssof.project;

import tecnico.ssof.project.TreeNode;

import java.util.List;

public class Analyzer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private String sliceFile;
	private String slice;
	private String vulnerability;
	private List<String> entryPoints;
	private List<String> validationFunctions;
	private List<String> sensitiveSinks;
	private TreeNode ast;
	
	public Analyzer(String sliceFile) {
		
		this.sliceFile = sliceFile;
	}
	
	

}

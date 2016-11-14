package tecnico.ssof.project;

import tecnico.ssof.project.TreeNode;
import tecnico.ssof.project.OurVisitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {

	public static void main(String[] args) throws IOException {
		
		// check arguments
		if(args.length != 1) {
			System.err.println("Incorrect number of arguments!");
			System.err.println("Usage: Analyzer <slice_file_path>!");
			System.exit(-1);
		}
		
		// get slice file path
		String slice_path = args[0];
		
		// create Analyzer
		Analyzer analyzer = new Analyzer(slice_path);
		
		// parse slice
		analyzer.parse();
	
	}
	
	private static final String PATTERN_FILE_PATH = "../../../../../../examples/pattern_sqli.txt"; 
	
	private String sliceFilePath; 				// path to the slice file
	private String vulnerability;				// vulnerability name (necessary???)
	private List<String> entryPoints;			// entry points
	private List<String> validationFunctions;	// sanitization functions
	private List<String> sensitiveSinks;		// sensitive sinks
	private TreeNode ast;						// tree build from the AST created by the PHP Parser
	
	public Analyzer(String path) {
		
		this.sliceFilePath = path;
		this.entryPoints = new ArrayList<String>();
		this.validationFunctions = new ArrayList<String>();
		this.sensitiveSinks = new ArrayList<String>();
	}
	
	// getters/setters
	
	public String getVulnerability() {
		return vulnerability;
	}

	public List<String> getEntryPoints() {
		return entryPoints;
	}

	public List<String> getValidationFunctions() {
		return validationFunctions;
	}

	public List<String> getSensitiveSinks() {
		return sensitiveSinks;
	}

	public TreeNode getAst() {
		return ast;
	}

	public void setAst(TreeNode ast) {
		this.ast = ast;
	}


	/// Creates AST for the given slice
	private void parse() throws IOException {
		
		// parse
		ParserFacade parser = new ParserFacade();
		parser.parse(new File(sliceFilePath));
		
		// build tree
		// treeBuilder = new TreeBuilder(new ParserFacade.parse(sliceFile));
		// treeBuilder.visit(this);
		
		// TODO
	}
	
	
	/// Runs over each pattern to search for vulnerabilities in the slice
	private void run() {
	
		// create File from PATTERN_FILE_PATH
		
		// for each pattern in pattern file
		//	1. get entryPoints, validationFunctions and sensitiveSinks
		//	2. treeWorker = new TreeWorker();
		//	3. treeWorker.visit(this);
		
		// TODO
	}
	
	
	/// Accept a visitor
	public void accept(OurVisitor v) {
		
		v.visit(this);
	}
}











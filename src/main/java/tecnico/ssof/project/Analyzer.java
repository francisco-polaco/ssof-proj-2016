package tecnico.ssof.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Analyzer {

	public static void main(String[] args) throws IOException {
		
		// check arguments
		if(args.length != 1) {
			System.err.println("Incorrect number of arguments!");
			System.err.println("Usage: java Analyzer <slice_file_path>!");
			System.exit(-1);
		}

        // get slice file path
		String slice_path = args[0];
		
		// create Analyzer
		Analyzer analyzer = new Analyzer(slice_path);
		
		// parse slice and give result as input to TreeBuilder for building the AST
		analyzer.parse();
        
        // method name is deceiving. this only parses the pattern file
        analyzer.processPatterns(args);

		// print file (with line numbers) and results
		System.out.println("\n--- Slice given as input ---\n");
		printSlice(new BufferedReader(new FileReader(analyzer.getSliceFilePath())));
		System.out.println("\n");

        analyzer.accept(new TreeWorker());


    }

	private static final String PATTERN_FILE_PATH = "patterns/pattern_all.txt"; 
	
	private String sliceFilePath; 				// path to the slice file
	private List<String> entryPoints;			// entry points
	private List<String> validationFunctions;	// sanitization functions
	private List<String> sensitiveSinks;		// sensitive sinks
	private TreeNode ast;						// tree build from the AST created by the PHP Parser

	private List<String> sliceLines = new ArrayList<>();

	private Analyzer(String path) throws IOException {

		this.sliceFilePath = path;
		this.entryPoints = new ArrayList<>();
		this.validationFunctions = new ArrayList<>();
		this.sensitiveSinks = new ArrayList<>();
		this.ast = new TreeNode("root", -1);			// dummy node to represent the tree root (has no relevant info)

		readSliceLines();
	}

	// getters/setters


	private String getSliceFilePath() {
		return sliceFilePath;
	}

	public List<String> getSliceLines() {
		return sliceLines;
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
		
		// parse (just for debug)
		//ParserFacade parser = new ParserFacade();
		//parser.parse(new File(sliceFilePath));
		
		// build tree
		TreeBuilder treeBuilder = new TreeBuilder(new ParserFacade(false).parse(new File(sliceFilePath)));
		this.accept(treeBuilder);
		
		// print AST to debug
		//TreeNode.print(ast);
	}
	
	
	/// Runs over each pattern to search for vulnerabilities in the slice
	private void processPatterns(String[] args) {
	
		List<String> file = readFile(PATTERN_FILE_PATH);
		int i = 0;
		String line = file.get(i);
		while(i < file.size()){
			if(line.equals("SQL injection") || line.equals("XSS")){
				i++; // skip the line with the name of the vulnerability
				
				String _entryPoints = file.get(i++);
				String _validationFunctions = file.get(i++);
				String _sensitiveSinks = file.get(i++);

				String[] _entryPointsList = _entryPoints.split(",");
				String[] _validationFunctionsList = _validationFunctions.split(",");
				String[] _sensitiveSinksList = _sensitiveSinks.split(",");


				Collections.addAll(this.entryPoints, _entryPointsList);
				Collections.addAll(this.validationFunctions, _validationFunctionsList);
				Collections.addAll(this.sensitiveSinks, _sensitiveSinksList);
			}
				i++; //skip blank line
		}
	}

	private List<String> readFile(String filename){
	  	List<String> records = new ArrayList<>();
		try
		{
		  	BufferedReader reader = new BufferedReader(new FileReader(filename));
		   	String line;
		    while ((line = reader.readLine()) != null){
		      records.add(line);
		    }
		    reader.close();
		    return records;
		}
		catch (Exception e){
		    System.err.format("Exception occurred trying to read '%s'.", filename);
		    e.printStackTrace();
		    return null;
		}
	}
	
	
	/// Prints slice file (with line numbers)
	private static void printSlice(BufferedReader buffer) throws IOException {
		
		String line;
		int line_no = 1;
		
	    while ((line = buffer.readLine()) != null) {
	    	
	    	System.out.println(String.valueOf(line_no) + ": " + line);
	    	line_no++;
	    }
	}
	
	/// Reads slice file to an array list (line by line)
	private void readSliceLines() throws IOException {
		 
		BufferedReader reader = new BufferedReader(new FileReader(this.sliceFilePath));
	   	String line;
	   	
	    while ((line = reader.readLine()) != null) {
	    	
	      this.sliceLines.add(line);
	    }
	    
	    reader.close();
	}
	
	
	/// Accept a visitor
	private void accept(OurVisitor v) {
		
		v.visit(this);

	}
	
	
	// Auxiliary for debug purposes
	private void printLists() {
		
		System.out.println("\n");
		
		System.out.println("Pattern entry points!");
		for(String s: entryPoints)
			System.out.println(s);
		
		System.out.println("Pattern validation functions!");
		for(String s: validationFunctions)
			System.out.println(s);
		
		System.out.println("Pattern sensitive sinks!");
		for(String s: sensitiveSinks)
			System.out.println(s);
	}
}











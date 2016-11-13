package tecnico.ssof.project;

import org.antlr.v4.runtime.RuleContext;

public class TreeBuilder extends OurVisitor {

	private RuleContext root;
	
	public TreeBuilder(RuleContext root) {
		
		this.root = root;
	}
	
	@Override
	public void visit(Analyzer analyzer) {
		// TODO Auto-generated method stub

	}
	
	/// Explore the given context (nothing more than a tree node from ANTLR4 parser)
	private void explore(RuleContext ctx) {
		
		// TODO
	}
}

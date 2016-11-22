package tecnico.ssof.project;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import tecnico.ssof.project.parser.PHPParser;

public class TreeBuilder extends OurVisitor {

	private RuleContext root;
    private boolean ignoringWrappers = true; // ignore rules that only call another rule
	
	public TreeBuilder(RuleContext root) {
		
		this.root = root;
	}
	
	@Override
	public void visit(Analyzer analyzer) {
		
		System.out.println("Just started building the tree!");
		
		explore(root, analyzer.getAst());
	}
	
	/// Explore the given context (nothing more than a tree node from ANTLR4 parser)
	private void explore(RuleContext ctx, TreeNode parentNode) {
		
		boolean toBeIgnored = ignoringWrappers
                && ctx.getChildCount() == 1
                && ctx.getChild(0) instanceof ParserRuleContext; // ignore (or not)
		
		TreeNode thisNode = null; // node for this method call
		
		// get rule name
		String ruleName = PHPParser.ruleNames[ctx.getRuleIndex()];
		
		// get line number
        int line = ((ParserRuleContext) ctx).getStart().getLine();
		
		// if not to be ignored, we need to build a new node
		if (!toBeIgnored && !ruleName.equals("htmlElement")) {
			
			thisNode = parentNode.addChild(ruleName, line); // add this rule as child
			System.out.println(thisNode.getText());
        }
		else
			thisNode = parentNode;
		
		// explore the children of this context
		for (int i = 0; i < ctx.getChildCount(); i++) {
			
            ParseTree child = ctx.getChild(i);
			
            if (child instanceof RuleContext) // explore this child
            	explore((RuleContext) child, thisNode);
        	else
        		if(child instanceof TerminalNode && !ruleName.equals("htmlElement")) // it's a token! just create node for it
        		{
        			TerminalNode leaf = (TerminalNode) child;
        			TreeNode dummy = thisNode.addChild(leaf.getText(), line);
        			System.out.println("TOKEN: " + dummy.getText());
        		}
		}
	}
}

package tecnico.ssof.project.printer;

import tecnico.ssof.project.parser.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTPrinter {

	private boolean ignoringWrappers = true; // do not print rules that only call another rule

    public void setIgnoringWrappers(boolean ignoringWrappers) {
        this.ignoringWrappers = ignoringWrappers;
    }

    public void print(RuleContext ctx) {
        explore(ctx, 0);
    }

    private void explore(RuleContext ctx, int indentation) {
    	
        boolean toBeIgnored = ignoringWrappers
                && ctx.getChildCount() == 1
                && ctx.getChild(0) instanceof ParserRuleContext; // ignore (or not)
        
        if (!toBeIgnored) {
			
            String ruleName = PHPParser.ruleNames[ctx.getRuleIndex()]; // get rule name
            
            for (int i = 0; i < indentation; i++) {
                System.out.print("  ");
            }
			System.out.println(ruleName);
        }
        
        for (int i=0;i<ctx.getChildCount();i++) {
			
            ParseTree element = ctx.getChild(i);
			
            if (element instanceof RuleContext)
                explore((RuleContext)element, indentation + (toBeIgnored ? 0 : 1));
            else
            	if(element instanceof TerminalNode) {
    				TerminalNode leaf = (TerminalNode) element;
    				
    				for (int j = 0; j < indentation + 1; j++) {
    	                System.out.print("  ");
    	            }
    				
    				System.out.println("TOKEN: " + leaf.getText());
                }
        }
    }
}

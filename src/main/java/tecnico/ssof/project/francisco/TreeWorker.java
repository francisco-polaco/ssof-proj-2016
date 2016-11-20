package tecnico.ssof.project.francisco;

import tecnico.ssof.project.Analyzer;
import tecnico.ssof.project.OurVisitor;
import tecnico.ssof.project.TreeNode;

import java.util.ArrayList;

public class TreeWorker extends OurVisitor {

    private ArrayList<String> taintedVariables = new ArrayList<>();
    private Analyzer analyzer;

    @Override
	public void visit(Analyzer analyzer) {
        System.out.println("==========FRANCISCO_VISITOR=============");
        this.analyzer = analyzer;
        if(analyzer.getAst().getChildCount() == 0){
            System.err.println("No ast found.");
            return;
        }
        TreeNode phpBlock = analyzer.getAst().getChildAt(0).getChildAt(0);
        if(!phpBlock.getText().equals("phpBlock")){
            System.err.println("no phpBlock found");
            return;
        }

        System.out.println("" + phpBlock.getChildCount());
        phpBlock.print();


        // Lets get all data input points
        for(int i = 0 ; i < phpBlock.getChildCount() ; i++){
            exploreForTaintedInput(phpBlock.getChildAt(i));
        }
        if(taintedVariables.size() == 0){
            System.out.println("There is no input functions! Hooray!");
            return;
        }



	}

	private void exploreForTaintedInput(TreeNode node){
        if(isAnAssignmentStatement(node)){
            // estamos perante uma situacao favoravel a uma atribuicao de variaveis mas
            try {
                exploreForInputFunctions(node.getChildAt(2));
            } catch (TaintedInputException e) {
                // AHAH I knew it, you shall go to the slammer!
                taintedVariables.add(node.getChildAt(0).getText());
            }
        }else if(node.getChildCount() == 1)
            exploreForTaintedInput(node.getChildAt(0));
        else{
            // nao sei o que fazer aqui
        }
    }

    private void exploreForInputFunctions(TreeNode node) throws TaintedInputException {
        if(isADangerousInputEntry(node)){
            // OMG!!! uma funcao de entrada perigosa!!
            throw new TaintedInputException();
        }else if(taintedVariables.contains(node.getText())){
            // atribuicao de uma variavel tainted a outra
            //OMG
            throw new TaintedInputException();
        }else{
            exploreForTaintedInput(node); //keep exploring
        }
    }

    private boolean isADangerousInputEntry(TreeNode node) {
        return node.getChildAt(0).isLeaf() && analyzer.getEntryPoints().contains(node.getChildAt(0).getText());
    }

    private boolean isAnAssignmentStatement(TreeNode node) {
        return isNotLeftRecursionExpression(node) && isAssignmentExpression(node) && isKeyedVariable(node, 0) && isKeyedVariable(node, 2);
    }

    private boolean isKeyedVariable(TreeNode node, int child) {
        return node.getChildAt(child).getText().equals("keyedVariable");
    }

    private boolean isNotLeftRecursionExpression(TreeNode node) {
        return !node.isLeaf() && node.getText().equals("notLeftRecursionExpression");
    }

    private boolean isAssignmentExpression(TreeNode node) {
        return !node.getChildAt(1).isLeaf() && node.getChildAt(1).getText().equals("assignmentOperator");
    }
}

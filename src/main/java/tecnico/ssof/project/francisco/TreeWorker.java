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

        // Check if this variables were escaped
        for(int i = 0 ; i < phpBlock.getChildCount() ; i++){
            exploreForSanitization(phpBlock.getChildAt(i));
        }

        if(taintedVariables.size() == 0){
            System.out.println("There is no bad variables! Hooray!");
            return;
        }


	}

	private void exploreForSanitization(TreeNode node){
        if(isAnAssignmentStatement(node) && isAFunctionCall(node.getChildAt(2))){
            try {
                exploreForSanitizationFunctions(node.getChildAt(2));
            } catch (SanitizationFunctionException e) {
                String varsName = node.getChildAt(0).getChildAt(0).getText();// getkeyedVariable.getToken.getText -> variable name
                if (taintedVariables.contains(varsName)){ // apaga variaveis que sejam escapadas
                    taintedVariables.remove(varsName);
                }
            }

        }else {
            for(int i = 0 ; i < node.getChildCount() ; i++)
                exploreForSanitization(node.getChildAt(i));
        }
    }

    private boolean isAFunctionCall(TreeNode childAt) {
        return !childAt.isLeaf() && childAt.getText().equals("functionCall");
    }

    private void exploreForSanitizationFunctions(TreeNode node) throws SanitizationFunctionException {
        if(isASanitizationFunction(node)){
            throw new SanitizationFunctionException(node.getLine());
        }
    }

    private boolean isASanitizationFunction(TreeNode node) {
        return node.getChildAt(0).isLeaf() && analyzer.getValidationFunctions().contains(node.getChildAt(0).getText());
    }

    private void exploreForTaintedInput(TreeNode node){
        if(isAnAssignmentStatement(node)) {
            // estamos perante uma situacao favoravel a uma atribuicao de variaveis mas
            try {
                exploreForInputFunctions(node.getChildAt(2));
            } catch (TaintedInputException e) {
                // AHAH I knew it, you shall go to the slammer!
                taintedVariables.add(node.getChildAt(0).getText());
            }
        }else{
            for(int i = 0 ; i < node.getChildCount() ; i++)
                exploreForTaintedInput(node.getChildAt(i));
        }
    }

    private void exploreForInputFunctionsInString(TreeNode node) throws TaintedInputException {
        // analyzing the whole string searching for tainted variables usages, or input functions
        for(int i = 0 ; i < node.getChildCount() ; i++) {
            if(node.getChildAt(i).getText().equals("keyedVariable")){
                TreeNode targetNode = node.getChildAt(i).getChildAt(0);
                if(targetNode.isLeaf()) {
                    String token = targetNode.getText();
                    if(analyzer.getEntryPoints().contains(token) || taintedVariables.contains(token)){
                        throw new TaintedInputException(targetNode.getLine());
                    }
                }
            }
        }
    }

    private void exploreForInputFunctions(TreeNode node) throws TaintedInputException {
        if(isADangerousInputEntry(node)){
            // OMG!!! uma funcao de entrada perigosa!!
            throw new TaintedInputException(node.getLine());
        }else if(taintedVariables.contains(node.getText())) {
            // atribuicao de uma variavel tainted a outra
            //OMG
            throw new TaintedInputException(node.getLine());
        }else if(!node.isLeaf() && node.getText().equals("string")){ // possivel concatenacao
            exploreForInputFunctionsInString(node.getChildAt(0));
        }else{
            exploreForTaintedInput(node); //keep exploring
        }
    }

    private boolean isADangerousInputEntry(TreeNode node) {
        return node.getChildAt(0).isLeaf() && analyzer.getEntryPoints().contains(node.getChildAt(0).getText());
    }

    private boolean isAnAssignmentStatement(TreeNode node) {
        return isNotLeftRecursionExpression(node) && isAssignmentExpression(node) && isKeyedVariable(node, 0);
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

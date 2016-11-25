package tecnico.ssof.project;

import org.antlr.v4.runtime.misc.NotNull;
import tecnico.ssof.project.exception.SanitizationFunctionException;
import tecnico.ssof.project.exception.SensitiveSinkWithVulnerabilityException;
import tecnico.ssof.project.exception.TaintedInputException;

import java.util.ArrayList;
import java.util.List;

public class TreeWorker extends OurVisitor {

    private ArrayList<String> taintedVariables = new ArrayList<>();
    private ArrayList<Integer> lineNumOfEscapeDangVars = new ArrayList<>();
    private Analyzer analyzer;

    @Override
	public void visit(@NotNull Analyzer analyzer) {
        this.analyzer = analyzer;
        if(analyzer.getAst() == null || analyzer.getAst().getChildCount() == 0){
            System.err.println("AST not found.");
            return;
        }

        TreeNode astRoot = analyzer.getAst();
        for(int i = 0 ; i < astRoot.getChildCount() ; i++){
            // Lets get all data input points
            exploreForTaintedInput(astRoot.getChildAt(i));

            // Check if this variables were escaped
            exploreForSanitization(astRoot.getChildAt(i));

            // Check if this variables are used in sensitive sinks
            try { // search for stings as args
                exploreForSensitiveSinks(astRoot.getChildAt(i));
            } catch (SensitiveSinkWithVulnerabilityException e) {
                System.out.println();
                System.out.println("Slice has a vulnerability at line " + e.getLine());
                return;
            }
        }

        // Text output to console
        System.out.println();
        if(lineNumOfEscapeDangVars.size() != 0) {
            List<String> lines = analyzer.getSliceLines();
            System.out.println("Slice's line where dangerous inputs are escaped:");
            for (Integer i : lineNumOfEscapeDangVars) {
                System.out.println("" + i + ": " + lines.get(i - 1));
            }
        }else
            System.out.println("With the patterns given, nothing in the code raised suspicious.");
	}

    private void exploreForSensitiveSinks(TreeNode node) throws SensitiveSinkWithVulnerabilityException {
        if(isAFunctionCall(node)){
            if(isASensitiveSinkFunction(node) && argumentsAreTainted(node)) {
                throw new SensitiveSinkWithVulnerabilityException(node.getLine());
            }
        }else if(isAnEchoStatement(node) && echoIsAConsiderSink()) {
            if(echoHasInputFunction(node) || echoHasTaintedVar(node)){
                throw new SensitiveSinkWithVulnerabilityException(node.getLine());
            }
        }else{
            for(int i = 0 ; i < node.getChildCount() ; i++)
                exploreForSensitiveSinks(node.getChildAt(i));
        }
    }

    private boolean echoHasTaintedVar(TreeNode node) {
        return isChildAtKeyedVariable(node, 1) &&
                taintedVariables.contains(node.getChildAt(1).getChildAt(0).getText());
    }

    private boolean echoHasInputFunction(TreeNode node) {
        return isChildAtKeyedVariable(node, 1) &&
                analyzer.getEntryPoints().contains(node.getChildAt(1).getChildAt(0).getText());
    }

    private boolean isAnEchoStatement(TreeNode node) {
        return !node.isLeaf() && node.getText().equals("echoStatement");
    }

    private boolean echoIsAConsiderSink() {
        return analyzer.getSensitiveSinks().contains("echo");
    }

    private boolean argumentsAreTainted(TreeNode node) {
        TreeNode argumentsNode = node.getChildAt(1);
        if(!argumentsNode.isLeaf() && argumentsNode.getText().equals("arguments")){
            for(int i = 0 ; i < argumentsNode.getChildCount() ; i++){
                if(isChildAtKeyedVariable(argumentsNode, i)){
                    TreeNode nodeArgumentToken = argumentsNode.getChildAt(i).getChildAt(0);
                    if(nodeArgumentToken.isLeaf() && taintedVariables.contains(nodeArgumentToken.getText()))
                        return true;
                }else if(isChildAtString(argumentsNode, i)){
                    try {
                        exploreForInputFunctionsInString(argumentsNode.getChildAt(i));
                    } catch (TaintedInputException e) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isASensitiveSinkFunction(TreeNode node) {
        return analyzer.getSensitiveSinks().contains(node.getChildAt(0).getChildAt(0).getText());
    }

    private void exploreForSanitization(TreeNode node){
        if(isAnAssignmentStatement(node) && isAFunctionCall(node.getChildAt(2))){
            try {
                exploreForSanitizationFunctions(node.getChildAt(2));
            } catch (SanitizationFunctionException e) {
                String varsName = e.getVarToSanitize();
                if (taintedVariables.contains(varsName)){ // apaga variaveis que sejam escapadas
                    taintedVariables.remove(varsName);
                    lineNumOfEscapeDangVars.add(node.getLine());
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
            throw new SanitizationFunctionException(node.getLine(), node.getChildAt(0).getChildAt(0).getText(), node.getChildAt(1).getChildAt(1).getChildAt(0).getText());
        }
    }

    private boolean isASanitizationFunction(TreeNode node) {
        return node.getChildAt(0).getChildAt(0).isLeaf() &&
                analyzer.getValidationFunctions().contains(node.getChildAt(0).getChildAt(0).getText());
    }

    private void exploreForTaintedInput(TreeNode node){

        if(isAnAssignmentStatement(node)) {
            // estamos perante uma situacao favoravel a uma atribuicao de variaveis mas
            try {
                exploreForInputFunctions(node.getChildAt(2));
            } catch (TaintedInputException e) {
                // AHAH I knew it, you shall go to the slammer!
                taintedVariables.add(node.getChildAt(0).getChildAt(0).getText()); //keyedVar.get Token
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
            exploreForInputFunctionsInString(node);
        }else{
            exploreForTaintedInput(node); //keep exploring
        }
    }

    private boolean isADangerousInputEntry(TreeNode node) {
        return node.getChildAt(0).isLeaf() && analyzer.getEntryPoints().contains(node.getChildAt(0).getText());
    }

    private boolean isAnAssignmentStatement(TreeNode node) {
        return isNotLeftRecursionExpression(node) && isAssignmentExpression(node) && isChildAtKeyedVariable(node, 0);
    }

    private boolean isChildAtKeyedVariable(TreeNode node, int child) {
        return !node.getChildAt(child).isLeaf() && node.getChildAt(child).getText().equals("keyedVariable");
    }

    private boolean isChildAtString(TreeNode node, int child) {
        return !node.getChildAt(child).isLeaf() && node.getChildAt(child).getText().equals("string");
    }

    private boolean isNotLeftRecursionExpression(TreeNode node) {
        return !node.isLeaf() && node.getText().equals("notLeftRecursionExpression");
    }

    private boolean isAssignmentExpression(TreeNode node) {
        return !node.getChildAt(1).isLeaf() && node.getChildAt(1).getText().equals("assignmentOperator");
    }
}

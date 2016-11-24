package tecnico.ssof.project.exception;

/**
 * Created by francisco on 20/11/2016.
 */
public class SanitizationFunctionException extends Exception {
    private int line;

    private String functionName;

    private String varToSanitize;

    public SanitizationFunctionException(){
        line = -1;
    }

    public SanitizationFunctionException(int line, String functionName, String varToSanitize){

        this.line = line;
        this.functionName = functionName;
        this.varToSanitize = varToSanitize;
    }
    public int getLine() {
        return line;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getVarToSanitize() {
        return varToSanitize;
    }

    @Override
    public String getMessage() {
        if(line == -1) return super.getMessage();
        else return "Sanitization function at line " + line;
    }
}

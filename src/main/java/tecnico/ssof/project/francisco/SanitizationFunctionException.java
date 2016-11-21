package tecnico.ssof.project.francisco;

/**
 * Created by francisco on 20/11/2016.
 */
public class SanitizationFunctionException extends Exception {
    private int line;

    public SanitizationFunctionException(){
        line = -1;
    }

    public SanitizationFunctionException(int line){

        this.line = line;
    }

    @Override
    public String getMessage() {
        if(line == -1) return super.getMessage();
        else return "sanitization function at line " + line;
    }
}

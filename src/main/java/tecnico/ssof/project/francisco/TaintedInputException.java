package tecnico.ssof.project.francisco;

/**
 * Created by francisco on 20/11/2016.
 */
public class TaintedInputException extends Exception {
    private int line;

    public TaintedInputException(){
        line = -1;
    }

    public  TaintedInputException(int line){

        this.line = line;
    }

    @Override
    public String getMessage() {
        if(line == -1) return super.getMessage();
        else return "Bad input at line " + line;
    }
}

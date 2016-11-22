package tecnico.ssof.project.francisco;

/**
 * Created by francisco on 20/11/2016.
 */
public class SensitiveSinkWithVulnException extends Exception {
    private int line;

    public SensitiveSinkWithVulnException(){
        line = -1;
    }

    public SensitiveSinkWithVulnException(int line){

        this.line = line;
    }

    @Override
    public String getMessage() {
        if(line == -1) return super.getMessage();
        else return "Sensitive sink with vuln at line " + line;
    }
}

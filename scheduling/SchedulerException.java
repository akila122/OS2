package scheduling;

public class SchedulerException extends Exception {
    
    private String msg2;
    private static final String msg1 = "Scheduler initialization error: "; 
    
    public SchedulerException(String msg2){
        this.msg2 = msg2;
    }
    
    public String toString(){
        return msg1+msg2;
    }
    
}


package scheduling;

public abstract class Pcb {

    public static Pcb[] RUNNING = new Pcb[10];

    public enum ProcessState {
        RUNNING, READY, BLOCKED, CREATED;
    }
    
    public abstract void preempt();

    public abstract int getId();

    public abstract int getPriority();

    public abstract void setTimeslice(long timeslice);

    public abstract long getExecutionTime();

    public abstract ProcessState getPreviousState();

    public abstract PcbData getPcbData();

    public abstract void setPcbData(PcbData pcbData);

    public static long getCurrentTime(){return 0;};

    public static int getProcessCount(){return 0;}

}

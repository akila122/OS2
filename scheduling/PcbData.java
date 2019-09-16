
package scheduling;


public class PcbData {

    private int cpuAffinity = -1;
    private long enterTime;
    private final Pcb myPcb;
    private final int pri;
    private final int id;
    
    public PcbData(int id,int pri,double sjf,double burst){
        
        this.id = id;
        this.pri = pri;
        this.sjf_Approx = sjf;
        this.cf_cpuBurst = burst;
        myPcb = null;
        
    }
    

    private double sjf_Approx;
    private boolean sjf_wasInterrupted;
    
    private double cf_cpuBurst;
    private int cf_burstCnt;
    
    private int mqfs_queue;

    public int getPriority(){
        return pri;
    }
    public int getId(){
        return id;
    }
    
    public void increaseBurst(){
        
        cf_cpuBurst+=myPcb.getExecutionTime();
        cf_burstCnt++;
        
    }

    public void resetBurst(){
        cf_cpuBurst /= cf_burstCnt;
        cf_burstCnt = 1;
    }
    
    public boolean wasInterrupted() {
        return sjf_wasInterrupted;
    }

    public void setInterrupt(boolean sjf_wasInterrupted) {
        this.sjf_wasInterrupted = sjf_wasInterrupted;
    }

    public double getCpuBurst() {
        return cf_cpuBurst;
    }

    public void setCpuBurst(long cpuBurst) {
        this.cf_cpuBurst = cpuBurst;
    }
    
    public int getMqfsQueue() {
        return mqfs_queue;
    }

    public void setMqfsQueue(int mqfs_queue) {
        this.mqfs_queue = mqfs_queue;
    }

    public double getSjfApprox() {
        return sjf_Approx;
    }

    public void setSjfApprox(double sjf_Approx) {
        this.sjf_Approx = sjf_Approx;
    }
    
            
    public Pcb getMyPcb() {
        return myPcb;
    }
    
    public PcbData(Pcb myPcb){
        
        this.myPcb = myPcb;
        this.pri = this.mqfs_queue = myPcb.getPriority();
        this.id = myPcb.getId();
        
        myPcb.setPcbData(this);
       
        
    }

    public int getCpuAffinity() {
        return cpuAffinity;
    }

    public void setCpuAffinity(int cpuAffinity) {
        this.cpuAffinity = cpuAffinity;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }
    
    
    public long getWaitingTime(){
        
        return enterTime - Pcb.getCurrentTime();
        
    }
   

    
    
    
    
}

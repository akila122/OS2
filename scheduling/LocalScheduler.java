
package scheduling;

public abstract class LocalScheduler {
    
    protected final int myCPU;
    
    protected final GlobalScheduler myGroup;
    
    protected abstract PcbData get();
    
    protected abstract void put(PcbData pcb);
    
    protected abstract int getLoadCnt();
    
    protected abstract void age(PcbQueue starvingQueue,long maxWaitingTime);
    
    protected LocalScheduler(int myCPU, GlobalScheduler myGroup){
        this.myCPU = myCPU;
        this.myGroup = myGroup;
    }
    
    
}

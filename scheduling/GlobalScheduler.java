
package scheduling;

public class GlobalScheduler extends Scheduler {

    private final int nP = Pcb.RUNNING.length;

    private final ALGOS myAlgo;

    private final long agePeriod;

    private final long maxWaitingTime;

    private final int cpuAffinityFactor;

    private long lastAgingDone;
    
    public String info(){
        return localArr[0].toString();
    }
    
    @Override
    public String toString(){
        return "SchedulerInfo:\n\n"
             + "MWT: "+maxWaitingTime+"\n"
             + "AP :"+agePeriod+"\n"
             + "CAF:"+cpuAffinityFactor+"\n";
    }
    
    private final LocalScheduler[] localArr = new LocalScheduler[nP];

    private final PcbQueue starvingQueue = new PcbQueue(PcbQueue.PRI_CMP);

    public GlobalScheduler(ALGOS myAlgo, long agePeriod, long maxWaitingTime, int cpuAffinityFactor,
                           double alpha,double dfltApprox,boolean isPreemptive, 
                           int[] timeSlices) {
        
      
        this.myAlgo = myAlgo;
        this.agePeriod = agePeriod;
        this.maxWaitingTime = maxWaitingTime;
        this.cpuAffinityFactor = cpuAffinityFactor;
        
        switch(myAlgo){
            case SJF:
                for(int i=0;i<nP;i++) localArr[i] = new ShortestJobFirst(i,this,isPreemptive,alpha,dfltApprox);
                break;
            case CF:
                for(int i=0;i<nP;i++) localArr[i] = new CompletelyFair(i,this);
                break;
            case MFQ:
                for(int i=0;i<nP;i++) localArr[i] = new MFQ(i,this,timeSlices);
                break;
        }
        
       
    }
    
    private boolean shouldAge() {

        long timeNow = Pcb.getCurrentTime();

        if (lastAgingDone + agePeriod <= timeNow) {
            lastAgingDone = timeNow;
            return true;
        } else return false;
    }

    private void age() {
        for (LocalScheduler ls : localArr) {
            ls.age(starvingQueue,maxWaitingTime);
        }
    }

    @Override
    public Pcb get(int cpuID) {

        if (cpuID < 0 || cpuID >= nP) {
            System.out.println("FATAL_ERROR : Invalid cpuID parameter in Scheduler.get(int cpuID) passed!");
            System.exit(1);
        }

        if (shouldAge()) age();
     
        PcbData ret = null;

        if (!starvingQueue.isEmpty()) {

            ret = starvingQueue.remove();
            ret.setCpuAffinity(cpuID);

        } else if (localArr[cpuID].getLoadCnt() > 0) {

            ret = localArr[cpuID].get();

        } else {

            LocalScheduler max = null;

            for (int i = 0; i < nP; i++) {
                if (max == null || localArr[i].getLoadCnt() > max.getLoadCnt()) {
                    max = localArr[i];
                }
            }

            if (max != null && max.getLoadCnt() > 0) {

                ret = max.get();
                ret.setCpuAffinity(cpuID);
            
            }

        }

        if (ret != null) return ret.getMyPcb();
        else return null;
        
    }

    @Override
    public void put(Pcb pcb) {
        
        if (pcb == null) {
            System.out.println("FATAL_ERROR: null value passed in Scheduler.put(Pcb pcb)!");
            System.exit(1);
        }
        
        if (pcb.getPcbData() == null) pcb.setPcbData(new PcbData(pcb));
        
        PcbData toPut = pcb.getPcbData();
        LocalScheduler ls = null,
                       minLs = null;
        int minCpuID = -1;
        
        for(int i=0; i<nP;i++){
                
                if (minLs == null || localArr[i].getLoadCnt() < minLs.getLoadCnt()){
                    
                    minLs = localArr[i];
                    minCpuID = i;
                    
                }
                
                if (minLs != null && minLs.getLoadCnt() == 0) break;
                
            }
        
        if(toPut.getCpuAffinity() == -1 ||
           localArr[minCpuID].getLoadCnt() == 0 ||
           localArr[toPut.getCpuAffinity()].getLoadCnt() - localArr[minCpuID].getLoadCnt() > cpuAffinityFactor ){
         
            ls = minLs;
            toPut.setCpuAffinity(minCpuID);
                
            } 
        else ls = localArr[toPut.getCpuAffinity()];

        ls.put(toPut);
        
        toPut.setEnterTime(Pcb.getCurrentTime());
        
    }

    public enum ALGOS { SJF, MFQ, CF };

}

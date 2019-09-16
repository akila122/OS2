package scheduling;

import java.util.Iterator;

public class ShortestJobFirst extends LocalScheduler {

    private final double alpha;

    public final double dfltApprox;

    private final boolean isPreemptive;

    private final PcbQueue q;

    
    public String toString(){
        String ret = myGroup.toString();
        ret+="TYPE: SJF\n"+"ALPHA: "+alpha+"\nDFLT: "+dfltApprox+"\nIS_PREEMP:"+isPreemptive+"\n";
        return ret;
    }
    public ShortestJobFirst(int myCPU, GlobalScheduler myGroup, boolean isPreemptive, double alpha,double dfltApprox) {
        
        super(myCPU, myGroup);
        this.isPreemptive = isPreemptive;
        this.alpha = alpha;
        this.dfltApprox = dfltApprox;
        if (isPreemptive) q = new PcbQueue(PcbQueue.SJF_PREEMPTIVE_CMP);
        else q = new PcbQueue(PcbQueue.SJF_CMP);
    
    }
    
    private void setNewApprox(PcbData toCalc) {

        if (toCalc.getMyPcb().getPreviousState() == Pcb.ProcessState.CREATED) toCalc.setSjfApprox(dfltApprox);
        else toCalc.setSjfApprox(alpha * toCalc.getMyPcb().getExecutionTime() + (1 - alpha) * toCalc.getSjfApprox());

    }

    @Override
    public PcbData get() {
        
       PcbData ret = q.remove();
       
       if (ret!=null) ret.getMyPcb().setTimeslice(0);
       
       return ret;

    }

    @Override
    public void put(PcbData pcb) {

        if (isPreemptive) {

            if (pcb.wasInterrupted()) {

                pcb.setInterrupt(false);
                if (pcb.getSjfApprox() > pcb.getMyPcb().getExecutionTime())
                    pcb.setSjfApprox(pcb.getSjfApprox() - pcb.getMyPcb().getExecutionTime());
                else setNewApprox(pcb);
            }
            else setNewApprox(pcb);
            
            if(Pcb.RUNNING[myCPU]!=null && Pcb.RUNNING[myCPU].getPriority() < pcb.getMyPcb().getPriority()){
                Pcb.RUNNING[myCPU].getPcbData().setInterrupt(true);
                Pcb.RUNNING[myCPU].preempt();//Is it safe to call it more than once?
            }
            

        }
        else setNewApprox(pcb);
        q.add(pcb);
    }

    @Override
    public int getLoadCnt() {
        return q.size();
    }

    @Override
    public void age(PcbQueue starvingQueue, long maxWaitingTime) {

        Iterator<PcbData> iter = q.getPriQ().iterator();

        while (iter.hasNext()) {
            PcbData tmp = iter.next();
            if (tmp.getWaitingTime() > maxWaitingTime) {

                iter.remove();
                starvingQueue.add(tmp);

            }
        }

    }

}

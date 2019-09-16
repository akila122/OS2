package scheduling;

import java.util.Iterator;

public class CompletelyFair extends LocalScheduler {

    
    private final PcbQueue q = new PcbQueue(PcbQueue.CF_CMP);
    
    private static final int TIMESLICE_GUARD = 1;

    public CompletelyFair(int myCPU, GlobalScheduler myGroup) {
        super(myCPU, myGroup);
    }
    
    public String toString(){
        String ret = myGroup.toString();
        ret+="TYPE: CF\n";
        return ret;
    }
    
    @Override
    protected PcbData get() {
        
        PcbData ret = q.remove();
        if(ret!=null){
            long newTimeSlice = ret.getWaitingTime()/Pcb.getProcessCount();
            if (newTimeSlice == 0) newTimeSlice = TIMESLICE_GUARD; //Change this!?
            ret.getMyPcb().setTimeslice(newTimeSlice);
        }
       return ret;
    }

    @Override
    protected void put(PcbData pcb) {
        
        Pcb.ProcessState state = pcb.getMyPcb().getPreviousState();
        if(state != Pcb.ProcessState.CREATED) pcb.increaseBurst();
        if(state == Pcb.ProcessState.BLOCKED) pcb.resetBurst();
        q.add(pcb);
        
    }

    @Override
    protected int getLoadCnt() {
        return q.size();
    }

    @Override
    protected void age(PcbQueue starvingQueue, long maxWaitingTime) {
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

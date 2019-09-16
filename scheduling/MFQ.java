/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author ra160248d
 */
public class MFQ extends LocalScheduler {

    private final int[] timeSlices;
    private final PcbQueue[] queues;
    private int load;

    @Override
    public String toString(){
        String ret = myGroup.toString();
        ret+="TYPE: MFQ\n"+"Q_CNT: "+queues.length+"\nTIME_SLICES: "+Arrays.toString(timeSlices)+"\n";
        return ret;
    }
    
    public MFQ(int myCPU, GlobalScheduler myGroup, int[] ts) {
        super(myCPU, myGroup);
        timeSlices = ts;
        queues = new PcbQueue[timeSlices.length];
        for(int i=0;i<queues.length;i++) queues[i] = new PcbQueue(PcbQueue.PRI_CMP);
    }

    @Override
    protected PcbData get() {
        
        PcbData ret = null;
        int qI = -1;
        for(int i=0;i<queues.length;i++){
            if (!queues[i].isEmpty()){
                ret = queues[i].remove();
                qI = i;
                break;
            }
        }
        
        if(ret!=null){
            ret.getMyPcb().setTimeslice(timeSlices[qI]);
            ret.setMqfsQueue(qI);
            load--;
            return ret;
        }
        else return null;
    }

    @Override
    protected void put(PcbData pcb) {

        Pcb.ProcessState state = pcb.getMyPcb().getPreviousState();
        if(state == Pcb.ProcessState.BLOCKED) decPri(pcb);
        else incPri(pcb);
        queues[pcb.getMqfsQueue()].add(pcb);
        load++;
    }

    @Override
    protected int getLoadCnt() {

        return load;

    }

    @Override
    protected void age(PcbQueue starvingQueue, long maxWaitingTime) {

        for (PcbQueue q : queues) {
            
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
    
    private void decPri(PcbData toTest){
        
        int pri = toTest.getMqfsQueue();
        if( ++pri >= queues.length ) pri = queues.length - 1;
        toTest.setMqfsQueue(pri);
        
    }
    
    private void incPri(PcbData toTest){
        
        int pri = toTest.getMqfsQueue();
        if (--pri < 0) pri = 0;
        toTest.setMqfsQueue(pri);
    }

}

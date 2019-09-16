
package scheduling;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PcbQueue {
    
    
    private final PriorityQueue<PcbData> q;
    
    public PcbQueue(Comparator<PcbData> cmp){
        
        q = new PriorityQueue<>(cmp);
        
    }
    
    public PcbData remove(){
        
        return q.remove();
        
    }
    
    public void add(PcbData pcb){
        
        q.add(pcb);
        
    }
    
    public boolean isEmpty(){
        return q.isEmpty();
    }
    
    public int size(){
        return q.size();
    }
    
    public PcbData peek(){
        return q.peek();
    }
    
    public PriorityQueue<PcbData> getPriQ(){
        return q;
    }
    
    public String toString(){
        String ret ="[ ";
        for(PcbData p : q ) ret+="P"+p.getId()+" ";
        ret+="]";
        return ret;
    }
    
    public static final Comparator<PcbData> PRI_CMP = (PcbData o1, PcbData o2) -> {
        int p1 = o1.getPriority(), 
            p2 = o2.getPriority();
        if (p1 > p2) return 1;
        else if (p1 < p2) return -1;
        else return 0;
    };
    public static final Comparator<PcbData> SJF_CMP = (PcbData o1, PcbData o2) -> {
        double p1 = o1.getSjfApprox(),
               p2 = o2.getSjfApprox();
        if (p1 < p2) return -1;
        else if (p1 > p2) return 1;
        else return PRI_CMP.compare(o1, o2);
    };
    public static final Comparator<PcbData> SJF_PREEMPTIVE_CMP = (PcbData o1, PcbData o2) -> {
        int p1 = o1.getPriority(), 
            p2 = o2.getPriority();
        if (p1 > p2) return 1;
        else if (p1 < p2) return -1;
        else return SJF_CMP.compare(o1, o2);
    };
    public static final Comparator<PcbData> CF_CMP = (PcbData o1, PcbData o2) -> {
        double p1 = o1.getCpuBurst(),
             p2 = o2.getCpuBurst();
        if (p1 < p2) return -1;
        else if (p1 > p2) return 1;
        else return PRI_CMP.compare(o1, o2);
    };

    
}

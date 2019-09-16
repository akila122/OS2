/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import java.util.Scanner;

public class SchedulerCreator {

    private String[] argv;
    private CreationType t;
    private GlobalScheduler.ALGOS myAlgo;
    private long agePeriod;
    private long maxWaitingTime;
    private int cpuAffinityFactor;
    private double alpha;
    private double dfltApprox;
    private boolean isPreemptive;
    private int[] timeSlices;

    public enum CreationType {
        INLINE, INTERACTIVE
    }

    public SchedulerCreator(CreationType t, String[] argv) throws SchedulerException {
        this.argv = argv;
        this.t = t;
        
        if (t == CreationType.INLINE){
            parseFields();
            testFields();
        }
        else startInteractiveInsertion();
    }

    public Scheduler create() {
        
        GlobalScheduler ret = new GlobalScheduler(myAlgo,agePeriod, maxWaitingTime,cpuAffinityFactor,
                                   alpha,dfltApprox,isPreemptive, timeSlices);
        
        System.out.println("\nScheduler successfully created!\n"+ret.info());
        
        return ret;
    }
    
    private boolean testAlpha(double a){
        
        return a >= 0 && a<=1;
        
    }
    
    private void parseFields() throws SchedulerException {
        try {
            switch (argv[1]) {
                case "SJF":
                    if (argv.length != 8) throw new SchedulerException("Too few arguments for Shortest Job First Scheduler passed!");
                    myAlgo = GlobalScheduler.ALGOS.SJF;
                    alpha = Double.parseDouble(argv[5]);
                    dfltApprox = Double.parseDouble(argv[6]);
                    isPreemptive = Boolean.parseBoolean(argv[7]);
                    break;
                case "MFQ":
                    if(argv.length < 6) throw new SchedulerException("Too few arguments for Multilevel Feedback Queue Scheduler passed!");
                    if(argv.length > 6) throw new SchedulerException("Too many arguments for Multilevel Feedback Queue Scheduler passed!");
                    myAlgo = GlobalScheduler.ALGOS.MFQ;
                    String[] ts = argv[5].split(",");
                    timeSlices = new int[ts.length];
                    for(int i = 0; i<timeSlices.length; i++) timeSlices[i] = Integer.parseInt(ts[i]);
                    break;
                case "CF":
                    if(argv.length < 5) throw new SchedulerException("Too few arguments for Multilevel Feedback Queue Scheduler passed!");
                    if(argv.length > 5) throw new SchedulerException("Too many arguments for Completley Fair Scheduler passed!");
                    myAlgo = GlobalScheduler.ALGOS.CF;
                    break;
                default:
                    throw new SchedulerException("Invalid arguments, unknown type of scheduler passed!");
            }
            agePeriod = Long.parseLong(argv[2]);
            maxWaitingTime = Long.parseLong(argv[3]);
            cpuAffinityFactor = Integer.parseInt(argv[4]);
        } catch (NumberFormatException e) {
            throw new SchedulerException("Invalid arguments, parsing has failed!");
        }
    }

    private void testFields() throws SchedulerException {
        
        if ( agePeriod <= 0 ) throw new SchedulerException("Age Period parameter that is used for Starvation handling cannot be negative or zero!");
        if ( maxWaitingTime <= 0 ) throw new SchedulerException("Maximum Waiting Time parameter that is used for Starvation handling cannot be negative or zero!");
        if(myAlgo == GlobalScheduler.ALGOS.SJF){
            if ( cpuAffinityFactor <= 0 ) throw new SchedulerException("CPU Affinity Factor parameter that is used for Cpu Cache balancing cannot be negative or zero!");
            if ( alpha < 0 || alpha > 1 ) throw new SchedulerException("Alpha parameter that is used for Shortest Job First Scheduler implementation cannot be lesser than zero or greater than one!");
            if ( dfltApprox <= 0 ) throw new SchedulerException("Initial CPU burst time prediction cannot be negative or zero!");
        }
        if(myAlgo == GlobalScheduler.ALGOS.MFQ)
            for(int i=0; i<timeSlices.length; i++ ){
                if( timeSlices[i] <=0 ) throw new SchedulerException("Time Slice paramater that is used for Multilevel Feedback Queue Scheduler cannot be negative or zero!");
        }
        
    }

    private void startInteractiveInsertion(){
        
        Scanner s = new Scanner(System.in);
        
        System.out.println("Interactive Scheduler initialization started!\n");
        
        System.out.println("Enter the Maximum Waiting Time that each process can wait in ready queue.");
        while((maxWaitingTime = s.nextLong())<=0) System.out.println("Maximum Waiting Time cannot be negative or zero.\nTry again.");
        
        System.out.println("Enter the period of aging mechanism activation:");
        while((agePeriod = s.nextLong())<=0) System.out.println("Period of aging mechanism activation cannot be negative or zero.\nTry again.");
        
        System.out.println("Enter the CPU Affinity Factor parameter. This is used to improve CPUs overall Cache hit ratio.");
        while((agePeriod = s.nextLong())<=0) System.out.println("Period of aging mechanism activation cannot be negative or zero.\nTry again.");

        boolean flag1 = true;
        while(flag1){
            System.out.println("Enter the type of Scheduling Algorithm the system should use.\nUse the following abbreviation for selecting algorithm:\n"
                             + "\t1) SJF - Shortest Job First Scheduling Algorithm\n"
                             + "\t2) MFQ - Multilevel Feedback Queue Scheduling Algorithm\n"
                             + "\t3) CF  - Completely Fair Scheduler Algorithm");
            String algo = s.next();
            switch(algo){
                case "SJF" :
                case "1"   :
                    myAlgo = GlobalScheduler.ALGOS.SJF;
                    flag1 = false;
                    break;
                case "MFQ" :
                case "2"   :
                    myAlgo = GlobalScheduler.ALGOS.MFQ;
                    flag1 = false;
                    break;
                case "CF" :
                case "3"  :
                    myAlgo = GlobalScheduler.ALGOS.CF;
                    flag1 = false;
                    break;
                default:
                    System.out.println("Invalid input.\nTry again.");
            }
        }
        
        switch(myAlgo){
            case SJF :
                System.out.println("Enter the Alpha parameter that will be used for Exponential Moving Average calculation of processes next CPU burst time prediction.");
                while(!testAlpha(alpha = s.nextDouble())) System.out.println("Alpha parameter must be between [0,1].\nTry again.");
                System.out.println("Enter the initial CPU burst time prediction");
                while((dfltApprox = s.nextDouble()) <= 0) System.out.println("Initial CPU burst time prediction cannot be negative or zero.\nTry again.");
                System.out.println("Enter 'TRUE'/'True'/'true' if you want to make this Scheduler preemptive. Note that any other input will be taken as false value.");
                String str = s.next();
                if (str.equals("TRUE") || str.equals("True") || str.equals("true")) isPreemptive = true;
                break;
            case MFQ:
                boolean flag2 = true;
                while(flag2){
                    System.out.println("Enter the Time Slices separated by ',' that this Scheduler will use.\n"
                            + "For example, entering: '2,4,8' will create three MFQ Scheduler queues with the given Time Slices respectively.");
                    String[] ts = s.next().split(",");
                    timeSlices = new int[ts.length];
                    try{
                        for(int i=0;i<timeSlices.length;i++){
         
                            timeSlices[i] = Integer.parseInt(ts[i]);
                            if(timeSlices[i]<=0) throw new Exception();
                            
                        }
                        flag2 = false;
                    }
                    catch(Exception e){System.out.println("Invalid Time Slice input.\nTry again.");}
                }
                break;
            default: break;
        }
    }
    
    public static void main(String[] argv){
        
        Scheduler.createScheduler(new String[]{"INLINE","CF","15","15","3","0.897","52","yes"});
    }

}
 
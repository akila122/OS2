
package scheduling;

public abstract class Scheduler {
    
    public abstract Pcb get(int cpuID);
    
    public abstract void put(Pcb pcb);
    
    
    
    public static Scheduler createScheduler(String[] argv){
        
        if(argv == null){
            System.out.println("Scheduler initialization error: null value passed as argument vector!");
            System.exit(1);
        }
        if(argv.length > 8){
            System.out.println("Scheduler initialization error: Too many arguments passed!");
            System.exit(1);
        }
        try{
            if (argv[0].equals("INLINE")) return new SchedulerCreator(SchedulerCreator.CreationType.INLINE,argv).create();
            else if(argv[0].equals("INTERACTIVE")) return new SchedulerCreator(SchedulerCreator.CreationType.INTERACTIVE,argv).create();
            else throw new SchedulerException("Invalid type of creation passed.");
        }
        catch(SchedulerException e){
            System.out.println(e);
            System.exit(1);
        }
        return null;
    }
    
    
}

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by KAMIL on 2015-06-01.
 */
public class Coordinator {

    List<Activity> activities;
    Boolean allProceduresFinished;

    Integer currentSimulationTime;
    Integer totalSimulationTime;
    Integer staffNum;
    Integer availableStaffNum;
    Integer clientsServiced;


    Random generator;
    static final int queueCapacity = 100;

    BlockingQueue<Client> clientsQueue;
    BlockingQueue<Client> ordersQueue;

    Machines machines;

    Coordinator(Integer totalSimulationTime, Integer smallPrinterNum, Integer largePrinterNum,
                Integer binderNum, Integer staffNum){

        machines = new Machines(smallPrinterNum, largePrinterNum, binderNum);

        this.currentSimulationTime = new Integer(0);
        this.totalSimulationTime = totalSimulationTime;
        this.staffNum = staffNum;
        this.availableStaffNum = new Integer(staffNum);
        this.clientsServiced = new Integer(0);

        allProceduresFinished = new Boolean(false);

        generator = new Random();

        clientsQueue = new ArrayBlockingQueue<Client>(queueCapacity);
        ordersQueue = new ArrayBlockingQueue<Client>(queueCapacity);

        generateInitialActivites();
    }

    private void generateInitialActivites(){
        activities = new LinkedList<Activity>();
        //@TODO Wygenerowanie listy dzia³an
        // AWARIE ////////////////////////////////////
        for(int i=0; i<machines.getLargePrinterNum(); i++){
            int newArrivalTime = 7 + generator.nextInt(8);
            activities.add(new Activity(Activity.headers.PRZYBYCIE_AWARII_DUZA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_LARGE), machines, i, -1, this) );
        }

        for(int i=0; i<machines.getSmallPrinterNum(); i++){
            int newArrivalTime = 2 + generator.nextInt(6);
            activities.add(new Activity(Activity.headers.PRZYBYCIE_AWARII_MALA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_SMALL), machines, i, -1, this) );
        }

        for(int i=0; i<machines.getBinderNum(); i++){
            int newArrivalTime = 4 + generator.nextInt(3);
            activities.add(new Activity(Activity.headers.PRZYBYCIE_AWARII_BINDOWNICA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_BIND), machines, i, -1, this) );
        }


    }

    void startSimulation(){

        reportTime();
        allProceduresFinished = activities.isEmpty();

        while(allProceduresFinished == false && currentSimulationTime<totalSimulationTime){
            currentSimulationTime = scanTime();
            reportTime();
            Boolean isAnyActivityDone = scanActivities();
            if(!isAnyActivityDone){
                Integer newTime = scanTimeForNextMinimalTime(currentSimulationTime);
                setTimeForBlockedActivities(currentSimulationTime, newTime);
            }
            allProceduresFinished = activities.isEmpty();
        }
    }

    private Integer scanTime(){
        Integer minActivityTime = activities.get(0).getStateChangeTime();
        for(Activity activity : activities)
            if(minActivityTime > activity.getStateChangeTime())
                minActivityTime = activity.getStateChangeTime();
        return minActivityTime;
    }

    private Integer scanTimeForNextMinimalTime(Integer currentSimulationTime){
        Integer minActivityTime = -1;
        for(Activity activity : activities)
            if((minActivityTime > activity.getStateChangeTime() || minActivityTime<0)
                    && activity.getStateChangeTime()!=currentSimulationTime)
                minActivityTime = activity.getStateChangeTime();
        return minActivityTime;
    }

    private void setTimeForBlockedActivities(Integer currentSimulationTime, Integer newTime){
        for(Activity activity : activities)
            if(currentSimulationTime == activity.getStateChangeTime())
                activity.setStateChangeTime(newTime);

    }

    private Boolean scanActivities(){

        Iterator <Activity> activityIterator = activities.iterator();
        List<Activity> activitiesToRemove = new LinkedList<Activity>();
        List<Activity> activitiesToAdd = new LinkedList<Activity>();

        Boolean isAnyActivityDone = false;

        while(activityIterator.hasNext()){
            Activity currentActivity = activityIterator.next();
            if(currentActivity.checkConditions()){
                //@TODO Sprawdz warunki unikalne dla kazdego headera (Boolean checkConditions(String header)) i uruchom procedure (startProcedure(String header)
                Activity newActivity = currentActivity.runProcedure();
                currentActivity.reportActivity();
                if(newActivity != null)
                    activitiesToAdd.add(newActivity);
                activitiesToRemove.add(currentActivity);
                isAnyActivityDone = true;
            }
        }

        activities.addAll(activitiesToAdd);
        activities.removeAll(activitiesToRemove);

        return isAnyActivityDone;
    }

    void reportTime(){
        System.out.println("Current Simulation Time: " + currentSimulationTime);
    }



    public Integer getCurrentSimulationTime(){
        return currentSimulationTime;
    }

    public Integer getAvailableStaffNumber(){
        return availableStaffNum;
    }

}

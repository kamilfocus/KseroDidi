import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by KAMIL on 2015-06-01.
 */
public class Coordinator {

    List<Activity> activities;
    Integer currentSimulationTime;
    Integer totalSimulationTime;
    Boolean allProceduresFinished;

    Machines machines;

    public class Activity{
        String header;
        Integer stateChangeTime;

        Activity(String header, Integer stateChangeTime){
            this.header = header;
            this.stateChangeTime = stateChangeTime;
        }

        Integer getStateChangeTime(){
            return stateChangeTime;
        }
    }

    Coordinator(Integer totalSimulationTime, Integer smallPrinterNum, Integer largePrinterNum, Integer binderNum){

        machines = new Machines(smallPrinterNum, largePrinterNum, binderNum);

        this.currentSimulationTime = new Integer(0);
        this.totalSimulationTime = totalSimulationTime;
        allProceduresFinished = new Boolean(false);

        generateInitialActivites();
    }

    private void generateInitialActivites(){
        activities = new LinkedList<Activity>();
        //@TODO Wygenerowanie listy dzia³an
    }

    void startSimulation(){

        allProceduresFinished = activities.isEmpty();

        while(allProceduresFinished == false && currentSimulationTime<totalSimulationTime){
            currentSimulationTime = scanTime();
            scanActivities();
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

    private void scanActivities(){
        Iterator <Activity> activityIterator = activities.iterator();
        while(activityIterator.hasNext()){
            Activity currentActivity = activityIterator.next();
            if(currentActivity.getStateChangeTime() == currentSimulationTime){
                //@TODO Sprawdz warunki unikalne dla kazdego headera (Boolean checkConditions(String header)) i uruchom procedure (startProcedure(String header)
                activityIterator.remove();
            }
        }
    }

}

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by KAMIL on 2015-06-01.
 */
public class Coordinator {

    List<Activity> activities;
    Boolean allProceduresFinished;


    Integer totalSimulationTime;
    Integer staffNum;

    Integer currentSimulationTime;
    Integer availableStaffNum;
    Integer clientsServiced;
    Integer ordersToPickUpNum;

    Random generator;
    static final int INFINITY = 100000000;

    Queue<Client> clientsQueue;
    Queue<Client> ordersQueue;
    List<Client> servicedClients;

    Machines machines;

    Coordinator(Integer totalSimulationTime, Integer smallPrinterNum, Integer largePrinterNum,
                Integer binderNum, Integer staffNum){

        machines = new Machines(smallPrinterNum, largePrinterNum, binderNum);

        this.currentSimulationTime = new Integer(0);
        this.totalSimulationTime = totalSimulationTime;
        this.staffNum = staffNum;
        this.availableStaffNum = new Integer(staffNum);
        this.clientsServiced = new Integer(0);
        this.ordersToPickUpNum = new Integer(0);

        allProceduresFinished = new Boolean(false);

        generator = new Random();

        servicedClients= new LinkedList<Client>();
        clientsQueue = new ArrayDeque<Client>();
        ordersQueue = new ArrayDeque<Client>();

        generateInitialActivites();
    }

    private void generateInitialActivites(){
        activities = new LinkedList<Activity>();
        //@TODO Wygenerowanie listy dzia³an
        for(int i=0; i<machines.getLargePrinterNum(); i++){
            // AWARIE ////////////////////////////////////
            int newArrivalTime = 7 + generator.nextInt(80);
            activities.add(new Activity(Activity.Headers.PRZYBYCIE_AWARII_DUZA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_LARGE), machines, i, -1,
                    this, true) );
            // WYMIANY ////////////////////////////////////
            activities.add(new Activity(Activity.Headers.WYMIANA_PAPIERU_DUZA_START,
                    INFINITY, new Client(Client.clientTypes.PAPER_EXCHANGE_LARGE), machines, i, -1,
                    this, false) );
            activities.add(new Activity(Activity.Headers.WYMIANA_TUSZU_DUZA_START,
                    INFINITY, new Client(Client.clientTypes.INK_EXCHANGE_LARGE), machines, i, -1,
                    this, false) );
        }

        for(int i=0; i<machines.getSmallPrinterNum(); i++){
            // AWARIE ////////////////////////////////////
            int newArrivalTime = 2 + generator.nextInt(60);
            activities.add(new Activity(Activity.Headers.PRZYBYCIE_AWARII_MALA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_SMALL), machines, i, -1,
                    this, true) );
            // WYMIANY ////////////////////////////////////
            activities.add(new Activity(Activity.Headers.WYMIANA_PAPIERU_MALA_START,
                    INFINITY, new Client(Client.clientTypes.PAPER_EXCHANGE_SMALL), machines, i, -1,
                    this, false) );
            activities.add(new Activity(Activity.Headers.WYMIANA_TUSZU_MALA_START,
                    INFINITY, new Client(Client.clientTypes.INK_EXCHANGE_SMALL), machines, i, -1,
                    this, false) );
        }

        for(int i=0; i<machines.getBinderNum(); i++){
            // AWARIE ////////////////////////////////////
            int newArrivalTime = 4 + generator.nextInt(30);
            activities.add(new Activity(Activity.Headers.PRZYBYCIE_AWARII_BINDOWNICA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_BIND), machines, i, -1,
                    this, true) );
        }

        // PRZYBYCIA KLIENTOW ////////////////////////////////////
        int newArrivalTime = 2 + generator.nextInt(2);
        activities.add(new Activity(Activity.Headers.PRZYBYCIE_KLIENTA_MALY_DRUK_BIND,
                newArrivalTime, new Client(Client.clientTypes.SMALL_PRINT_BIND), machines, 0, -1,
                this, true) );

        // KLIENCI - START ////////////////////////////////////
        activities.add(new Activity(Activity.Headers.OBSLUGA_KLIENTA_MALY_DRUK_BIND_START,
                INFINITY, new Client(Client.clientTypes.SMALL_PRINT_BIND), machines, 0, -1,
                this, false) );
        activities.add(new Activity(Activity.Headers.WYJSCIE_KLIENTA_MALY_DRUK_BIND_START,
                INFINITY, new Client(Client.clientTypes.SMALL_PRINT_BIND), machines, 0, -1,
                this, false) );

        //DRUK+BIND - START ////////////////////////////////////
        activities.add(new Activity(Activity.Headers.DRUKOWANIE_MALA_START,
                INFINITY, new Client(Client.clientTypes.NOT_DETERMINED), machines, 0, -1,
                this, false) );
        activities.add(new Activity(Activity.Headers.BINDOWANIE_START,
                INFINITY, new Client(Client.clientTypes.NOT_DETERMINED), machines, 0, -1,
                this, false) );


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
                if(currentActivity.isRemovable())
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

    public Integer getOrdersToPickUpNum() { return ordersToPickUpNum; }

    void addToClientQueue(Client newClient){
        clientsQueue.add(newClient);
    }

    Client getFirstClientInQueue(){
        return clientsQueue.poll();
    }

    Client checkFirstClientInQueue(){
        return clientsQueue.peek();
    }

    Integer clientQueueSize(){
        return clientsQueue.size();
    }

    void addToServicedClients(Client newClient){
        servicedClients.add(newClient);
    }

    void removeFromServicedClients(Client.clientStates clientState){

        Iterator <Client> servicedClientsIterator = servicedClients.iterator();
        while(servicedClientsIterator.hasNext()){
            if(servicedClientsIterator.next().getClientState() == clientState)
                break;
        }

        if(servicedClientsIterator!=null)
            servicedClientsIterator.remove();
    }

    Boolean hasAnyServicedClientState(Client.clientStates clientState){
        Iterator <Client> servicedClientsIterator = servicedClients.iterator();
        Boolean result = false;
        while(servicedClientsIterator.hasNext()){
            if(servicedClientsIterator.next().getClientState() == clientState)
                result = true;
        }
        return result;
    }

    Client getFirstServicedClient(Client.clientStates clientState){
        Iterator <Client> servicedClientsIterator = servicedClients.iterator();
        while(servicedClientsIterator.hasNext()){
            Client currClient = servicedClientsIterator.next();
            if(currClient.getClientState() == clientState)
                return currClient;
        }
        return null;
    }

}

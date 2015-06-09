import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by KAMIL on 2015-06-01.
 */
public class Coordinator {

    List<Activity> activities;
    Boolean allProceduresFinished;
    Gui gui;

    Integer totalSimulationTime;
    Integer staffNum;
    Integer[] staffBusyTime;

    Integer currentSimulationTime;
    Integer availableStaffNum;
    Integer clientsServiced;
    Integer ordersToPickUpNum;

    Integer smallPagesPrinted;
    Integer largePagesPrinted;
    Integer bindedNum;

    Random generator;
    static final int INFINITY = 100000000;

    Queue<Client> clientsQueue;
    Queue<Client> ordersQueue;
    List<Client> servicedClients;

    Machines machines;

    Coordinator(Integer totalSimulationTime, Integer smallPrinterNum, Integer largePrinterNum,
                Integer binderNum, Integer staffNum, Gui gui) {

        machines = new Machines(smallPrinterNum, largePrinterNum, binderNum);
        this.gui = gui;
        this.currentSimulationTime = new Integer(0);
        this.totalSimulationTime = totalSimulationTime;
        this.staffNum = staffNum;
        this.availableStaffNum = new Integer(staffNum);
        this.staffBusyTime = new Integer[staffNum + 2];
        for (int i = 0; i <= staffNum + 1; i++) {
            staffBusyTime[i] = 0;
        }

        this.clientsServiced = new Integer(0);
        this.ordersToPickUpNum = new Integer(0);

        this.smallPagesPrinted = new Integer(0);
        this.largePagesPrinted = new Integer(0);
        this.bindedNum = new Integer(0);


        allProceduresFinished = new Boolean(false);

        generator = new Random();

        servicedClients = new LinkedList<Client>();
        clientsQueue = new ArrayDeque<Client>();
        ordersQueue = new ArrayDeque<Client>();

        generateInitialActivites();
    }

    private void generateInitialActivites() {
        activities = new LinkedList<Activity>();
        //@TODO Wygenerowanie listy dzia�an
        for (int i = 0; i < machines.getLargePrinterNum(); i++) {
            // AWARIE ////////////////////////////////////
            int newArrivalTime = 7 + generator.nextInt(80);
            activities.add(new Activity(Activity.Headers.PRZYBYCIE_AWARII_DUZA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_LARGE), machines, i, -1,
                    this, true, gui));
            // WYMIANY ////////////////////////////////////
            activities.add(new Activity(Activity.Headers.WYMIANA_PAPIERU_DUZA_START,
                    INFINITY, new Client(Client.clientTypes.PAPER_EXCHANGE_LARGE), machines, i, -1,
                    this, false, gui));
            activities.add(new Activity(Activity.Headers.WYMIANA_TUSZU_DUZA_START,
                    INFINITY, new Client(Client.clientTypes.INK_EXCHANGE_LARGE), machines, i, -1,
                    this, false, gui));
        }

        for (int i = 0; i < machines.getSmallPrinterNum(); i++) {
            // AWARIE ////////////////////////////////////
            int newArrivalTime = 2 + generator.nextInt(60);
            activities.add(new Activity(Activity.Headers.PRZYBYCIE_AWARII_MALA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_SMALL), machines, i, -1,
                    this, true, gui));
            // WYMIANY ////////////////////////////////////
            activities.add(new Activity(Activity.Headers.WYMIANA_PAPIERU_MALA_START,
                    INFINITY, new Client(Client.clientTypes.PAPER_EXCHANGE_SMALL), machines, i, -1,
                    this, false, gui));
            activities.add(new Activity(Activity.Headers.WYMIANA_TUSZU_MALA_START,
                    INFINITY, new Client(Client.clientTypes.INK_EXCHANGE_SMALL), machines, i, -1,
                    this, false, gui));
        }

        for (int i = 0; i < machines.getBinderNum(); i++) {
            // AWARIE ////////////////////////////////////
            int newArrivalTime = 4 + generator.nextInt(30);
            activities.add(new Activity(Activity.Headers.PRZYBYCIE_AWARII_BINDOWNICA,
                    newArrivalTime, new Client(Client.clientTypes.BREAKDOWN_BIND), machines, i, -1,
                    this, true, gui));
        }

        // PRZYBYCIA KLIENTOW ////////////////////////////////////
        int newArrivalTime = 2 + generator.nextInt(2);
        activities.add(new Activity(Activity.Headers.PRZYBYCIE_KLIENTA_MALY_DRUK_BIND,
                newArrivalTime, new Client(Client.clientTypes.SMALL_PRINT_BIND), machines, 0, -1,
                this, true, gui));
        newArrivalTime = 5 + generator.nextInt(5);
        activities.add(new Activity(Activity.Headers.PRZYBYCIE_KLIENTA_MALY_DRUK_DUZY_DRUK,
                newArrivalTime, new Client(Client.clientTypes.SMALL_PRINT_LARGE_PRINT), machines, 0, -1,
                this, true, gui));
        newArrivalTime = 10 + generator.nextInt(10);
        activities.add(new Activity(Activity.Headers.PRZYBYCIE_ZAMOWIENIA_ELEKTRONICZNEGO,
                newArrivalTime, new Client(Client.clientTypes.ELECTRONIC_ORDER), machines, 0, -1,
                this, true, gui));

        // KLIENCI - START ////////////////////////////////////
        activities.add(new Activity(Activity.Headers.OBSLUGA_KLIENTA_MALY_DRUK_BIND_START,
                INFINITY, new Client(Client.clientTypes.SMALL_PRINT_BIND), machines, 0, -1,
                this, false, gui));
        activities.add(new Activity(Activity.Headers.WYJSCIE_KLIENTA_MALY_DRUK_BIND_START,
                INFINITY, new Client(Client.clientTypes.SMALL_PRINT_BIND), machines, 0, -1,
                this, false, gui));

        activities.add(new Activity(Activity.Headers.OBSLUGA_KLIENTA_MALY_DRUK_DUZY_DRUK_START,
                INFINITY, new Client(Client.clientTypes.SMALL_PRINT_LARGE_PRINT), machines, 0, -1,
                this, false, gui));
        activities.add(new Activity(Activity.Headers.WYJSCIE_KLIENTA_MALY_DRUK_DUZY_DRUK_START,
                INFINITY, new Client(Client.clientTypes.SMALL_PRINT_LARGE_PRINT), machines, 0, -1,
                this, false, gui));

        activities.add(new Activity(Activity.Headers.OBSLUGA_ZAMOWIENIA_ELEKTRONICZEGO_START,
                INFINITY, new Client(Client.clientTypes.ELECTRONIC_ORDER), machines, 0, -1,
                this, false, gui));
        activities.add(new Activity(Activity.Headers.ODLOZENIE_ZAMOWIENIA_ELEKTRONICZEGO_START,
                INFINITY, new Client(Client.clientTypes.ELECTRONIC_ORDER), machines, 0, -1,
                this, false, gui));

        activities.add(new Activity(Activity.Headers.OBSLUGA_KLIENTA_ODB_ZAM_START,
                INFINITY, new Client(Client.clientTypes.RECEIVE_ORDER), machines, 0, -1,
                this, false, gui));

        //DRUK+BIND - START ////////////////////////////////////
        activities.add(new Activity(Activity.Headers.DRUKOWANIE_MALA_START,
                INFINITY, new Client(Client.clientTypes.NOT_DETERMINED), machines, 0, -1,
                this, false, gui));
        activities.add(new Activity(Activity.Headers.DRUKOWANIE_DUZA_START,
                INFINITY, new Client(Client.clientTypes.NOT_DETERMINED), machines, 0, -1,
                this, false, gui));
        activities.add(new Activity(Activity.Headers.BINDOWANIE_START,
                INFINITY, new Client(Client.clientTypes.NOT_DETERMINED), machines, 0, -1,
                this, false, gui));


    }

    Integer startSimulation() {

        //reportTime(gui);
        allProceduresFinished = activities.isEmpty();

        while (allProceduresFinished == false && currentSimulationTime < totalSimulationTime) {
            currentSimulationTime = scanTime();
            reportTime(gui);

            Boolean isAnyActivityDone = scanActivities();

            while (isAnyActivityDone) {
                isAnyActivityDone = scanActivities();
            }
            Integer newTime = scanTimeForNextMinimalTime(currentSimulationTime);
            setTimeForBlockedActivities(currentSimulationTime, newTime);
            calculateBusyTime(currentSimulationTime, newTime);
            calculateStaffBusyTime(currentSimulationTime, newTime);
            allProceduresFinished = activities.isEmpty();
        }

        return currentSimulationTime;
    }

    private Integer scanTime() {
        Integer minActivityTime = activities.get(0).getStateChangeTime();
        for (Activity activity : activities)
            if (minActivityTime > activity.getStateChangeTime())
                minActivityTime = activity.getStateChangeTime();
        return minActivityTime;
    }

    private Integer scanTimeForNextMinimalTime(Integer currentSimulationTime) {
        Integer minActivityTime = -1;
        for (Activity activity : activities)
            if ((minActivityTime > activity.getStateChangeTime() || minActivityTime < 0)
                    && activity.getStateChangeTime() != currentSimulationTime)
                minActivityTime = activity.getStateChangeTime();
        return minActivityTime;
    }

    private void setTimeForBlockedActivities(Integer currentSimulationTime, Integer newTime) {
        for (Activity activity : activities)
            if (currentSimulationTime == activity.getStateChangeTime())
                activity.setStateChangeTime(newTime);

    }

    private Boolean scanActivities() {

        Iterator<Activity> activityIterator = activities.iterator();
        List<Activity> activitiesToRemove = new LinkedList<Activity>();
        List<Activity> activitiesToAdd = new LinkedList<Activity>();

        Boolean isAnyActivityDone = false;

        while (activityIterator.hasNext()) {
            Activity currentActivity = activityIterator.next();

            if (currentActivity.checkConditions()) {
                //@TODO Sprawdz warunki unikalne dla kazdego headera (Boolean checkConditions(String header)) i uruchom procedure (startProcedure(String header)
                Activity newActivity = currentActivity.runProcedure();
                currentActivity.reportActivity(gui);
                if (newActivity != null)
                    activitiesToAdd.add(newActivity);
                if (currentActivity.isRemovable())
                    activitiesToRemove.add(currentActivity);
                isAnyActivityDone = true;
            }
        }

        activities.addAll(activitiesToAdd);
        activities.removeAll(activitiesToRemove);

        return isAnyActivityDone;
    }

    public Integer getCurrentSimulationTime() {
        return currentSimulationTime;
    }

    public Integer getAvailableStaffNumber() {
        return availableStaffNum;
    }

    public Integer getOrdersToPickUpNum() {
        return ordersToPickUpNum;
    }

    void addToClientQueue(Client newClient) {
        clientsQueue.add(newClient);
    }

    Client getFirstClientInQueue() {
        return clientsQueue.poll();
    }

    void addToOrderQueue(Client newClient) {
        ordersQueue.add(newClient);
    }

    Client getFirstOrderInQueue() {
        return ordersQueue.poll();
    }

    Client checkFirstClientInQueue() {
        return clientsQueue.peek();
    }

    Integer clientQueueSize() {
        return clientsQueue.size();
    }

    Integer orderQueueSize() {
        return ordersQueue.size();
    }

    void addToServicedClients(Client newClient) {
        servicedClients.add(newClient);
    }

    void removeFromServicedClients(Client.clientStates clientState) {

        Iterator<Client> servicedClientsIterator = servicedClients.iterator();
        while (servicedClientsIterator.hasNext()) {
            if (servicedClientsIterator.next().getClientState() == clientState)
                break;
        }

        if (servicedClientsIterator != null)
            servicedClientsIterator.remove();
    }

    Boolean hasAnyServicedClientState(Client.clientStates clientState) {
        Iterator<Client> servicedClientsIterator = servicedClients.iterator();
        Boolean result = false;
        while (servicedClientsIterator.hasNext()) {
            if (servicedClientsIterator.next().getClientState() == clientState)
                result = true;
        }
        return result;
    }

    Boolean hasAnyServicedClientState(Client.clientStates[] clientStates) {
        Iterator<Client> servicedClientsIterator = servicedClients.iterator();
        Boolean result = false;
        while (servicedClientsIterator.hasNext()) {
            Client.clientStates currentState = servicedClientsIterator.next().getClientState();
            for (Client.clientStates matchHeaderState : clientStates)
                if (matchHeaderState == currentState)
                    result = true;
        }
        return result;
    }

    Client getFirstServicedClient(Client.clientStates clientState) {
        Iterator<Client> servicedClientsIterator = servicedClients.iterator();
        while (servicedClientsIterator.hasNext()) {
            Client currClient = servicedClientsIterator.next();
            if (currClient.getClientState() == clientState)
                return currClient;
        }
        return null;
    }

    Client getFirstServicedClient(Client.clientStates[] clientStates) {
        Iterator<Client> servicedClientsIterator = servicedClients.iterator();
        while (servicedClientsIterator.hasNext()) {
            Client currClient = servicedClientsIterator.next();
            for (Client.clientStates matchHeaderState : clientStates)
                if (matchHeaderState == currClient.getClientState())
                    return currClient;
        }
        return null;
    }

    void reportTime(Gui gui) {
        System.out.println("Current Simulation Time: " + currentSimulationTime);
        gui.resultTArea.append("\n \n Aktualny czas: " + Integer.toString(currentSimulationTime) + "\n");
    }

    void calculateBusyTime(Integer oldtime, Integer newTime) {
        for (int i = 0; i < machines.getLargePrinterNum(); i++) {
            machines.getLargePrinter(i).calculateBusyTime(oldtime, newTime);
        }
        for (int i = 0; i < machines.getSmallPrinterNum(); i++) {
            machines.getSmallPrinter(i).calculateBusyTime(oldtime, newTime);
        }
        for (int i = 0; i < machines.getBinderNum(); i++) {
            machines.getBindingMachine(i).calculateBusyTime(oldtime, newTime);
        }
    }

    void calculateStaffBusyTime(Integer oldtime, Integer newTime) {
        staffBusyTime[availableStaffNum + 1] += (newTime - oldtime);
    }

    void printBusyTimeinPercent() {
        for (int i = 0; i < machines.getLargePrinterNum(); i++) {
            System.out.println("Large Printer Busy Time with ID " + i + " : " +
                    100 * new Float(machines.getLargePrinter(i).getTotalBusyTime().floatValue()
                            / currentSimulationTime.floatValue()) + "%");
        }
        for (int i = 0; i < machines.getSmallPrinterNum(); i++) {
            System.out.println("Small Printer Busy Time with ID " + i + " : " +
                    100 * new Float(machines.getSmallPrinter(i).getTotalBusyTime().floatValue()
                            / currentSimulationTime.floatValue()) + "%");
        }
        for (int i = 0; i < machines.getBinderNum(); i++) {
            System.out.println("Binding Machine Busy Time with ID " + i + " : " +
                    100 * new Float(machines.getBindingMachine(i).getTotalBusyTime().floatValue()
                            / currentSimulationTime.floatValue()) + "%");
        }
    }

    void printStaffBusyTimeinPercent() {
        for (int i = 1; i <= staffNum + 1; i++) {
            System.out.println("Time when available staff =  " + (i - 1) + " : " +
                    100 * new Float(staffBusyTime[i].floatValue()
                            / currentSimulationTime.floatValue()) + "%");
        }
    }

    void reportSimulationSummary() {

        System.out.println("Small pages printed: " + smallPagesPrinted);
        System.out.println("Large pages printed: " + largePagesPrinted);
        System.out.println("Binded: " + bindedNum);
        System.out.println("Clients Serviced: " + clientsServiced);
        System.out.println("Clients in queue: " + clientQueueSize());
        System.out.println("Orders in queue: " + orderQueueSize());
        System.out.println("Machine busy-state statistics");
        printBusyTimeinPercent();
        System.out.println("Staff busy-state statistics");
        printStaffBusyTimeinPercent();
    }

    void printStats(Gui gui){
        for (int i = 0; i < machines.getLargePrinterNum(); i++) {
            gui.statistics.statsLarge.append("\n" + "Zajetosc duzej drukarki o ID " + i + " : " +
                    100 * new Float(machines.getLargePrinter(i).getTotalBusyTime().floatValue()
                            / currentSimulationTime.floatValue()) + "%");
        }
        for (int i = 0; i < machines.getSmallPrinterNum(); i++) {
            gui.statistics.statsSmall.append("\n" + "Zajetosc malej drukarki o ID " + i + " : " +
                    100 * new Float(machines.getSmallPrinter(i).getTotalBusyTime().floatValue()
                            / currentSimulationTime.floatValue()) + "%");
        }
        for (int i = 0; i < machines.getBinderNum(); i++) {
            gui.statistics.statsBind.append("\n" + "Zajetosc bindownicy o ID " + i + " : " +
                    100 * new Float(machines.getBindingMachine(i).getTotalBusyTime().floatValue()
                            / currentSimulationTime.floatValue()) + "%");
        }
    }

    void printStaff(Gui gui) {
        for (int i = 1; i <= staffNum + 1; i++) {
            gui.statistics.statsStaff.append("\n" + "Wolnych pracowników =  " + (i - 1) + " : " +
                    100 * new Float(staffBusyTime[i].floatValue()
                            / currentSimulationTime.floatValue()) + "%");
        }
    }

    void printResults(Gui gui) {
        gui.smallPages.setText("Ilosc wydrukowanych stron na malej drukarce: " + smallPagesPrinted);
        gui.largePages.setText("Ilosc wydrukowanych stron na duzej drukarce: " + largePagesPrinted);
        gui.bind.setText("Ilosc zbindowanych stron: " + bindedNum);
        gui.clients.setText("Ilosc obsluzonych klientow: " + clientsServiced);
        gui.clientsQueue.setText("Ilosc klientow w kolejce: " + clientQueueSize());
        gui.ordersQueue.setText("Ilosc zamowien w kolejce: " + orderQueueSize());

    }

}
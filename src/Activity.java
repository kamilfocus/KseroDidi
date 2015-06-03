import java.util.Random;

/**
 * Created by KAMIL on 2015-06-02.
 */
public class Activity{

    Coordinator parentCoordinator;

    Headers header;
    Integer stateChangeTime;
    Boolean removable;

    Integer firstMachineID;
    Integer secondMachineID;

    Client client;
    Machines machines;

    static final int NEXT_BREAKDOWN_TIME = 100;
    static final int NEXT_CLIENT_TIME = 20;
    static final int PAPER_AND_INK_EXCHANGE_THRESHOLD = 5;

    public enum Headers {
        // AWARIE ////////////////////////////////////
        PRZYBYCIE_AWARII_DUZA,
        PRZYBYCIE_AWARII_MALA,
        PRZYBYCIE_AWARII_BINDOWNICA,
        OBSLUGA_AWARII_DUZA_START,
        OBSLUGA_AWARII_DUZA_KONIEC,
        OBSLUGA_AWARII_MALA_START,
        OBSLUGA_AWARII_MALA_KONIEC,
        OBSLUGA_AWARII_BINDOWNICA_START,
        OBSLUGA_AWARII_BINDOWNICA_KONIEC,
        // WYMIANY ////////////////////////////////////
        WYMIANA_PAPIERU_DUZA_START,
        WYMIANA_PAPIERU_DUZA_KONIEC,
        WYMIANA_PAPIERU_MALA_START,
        WYMIANA_PAPIERU_MALA_KONIEC,
        WYMIANA_TUSZU_DUZA_START,
        WYMIANA_TUSZU_DUZA_KONIEC,
        WYMIANA_TUSZU_MALA_START,
        WYMIANA_TUSZU_MALA_KONIEC,
        // KLIENCI ////////////////////////////////////
        PRZYBYCIE_KLIENTA_MALY_DRUK_BIND,
        PRZYBYCIE_KLIENTA_MALY_DRUK_DUZY_DRUK,
        PRZYBYCIE_ZAMOWIENIA_ELEKTRONICZNEGO,
        PRZYBYCIE_KLIENTA_ODB_ZAM,
        OBSLUGA_KLIENTA_MALY_DRUK_BIND_START,
        OBSLUGA_KLIENTA_MALY_DRUK_BIND_KONIEC,
        WYJSCIE_KLIENTA_MALY_DRUK_BIND_START,
        WYJSCIE_KLIENT_MALY_DRUK_BIND_KONIEC,
        // DRUK+BIND ////////////////////////////////////
        DRUKOWANIE_MALA_START,
        DRUKOWANIE_MALA_KONIEC,
        BINDOWANIE_START,
        BINDOWANIE_KONIEC
    }

    Activity(Headers header, Integer stateChangeTime, Client client,
             Machines machines, Integer firstMachineID, Integer secondMachineID,
             Coordinator parentCoordinator, Boolean removable){

        this.header = header;
        this.stateChangeTime = stateChangeTime;
        this.client = client;
        this.machines = machines;
        this.firstMachineID = firstMachineID;
        this.secondMachineID = secondMachineID;

        this.parentCoordinator = parentCoordinator;
        this.removable = removable;
    }

    Integer getStateChangeTime(){ return stateChangeTime;}

    Boolean isRemovable(){ return removable; }

    void setStateChangeTime(Integer time){
        stateChangeTime = time;
    }

    Random getRandomGenerator(){
        return machines.getRandomGenerator();
    }

    Boolean isEqualToCurrentSimulationTime(){
        return (stateChangeTime == parentCoordinator.getCurrentSimulationTime());
    }

    Boolean isStaffAvailable(){
        return ((parentCoordinator.getAvailableStaffNumber()>0));
    }

    Machines.LargePrinter getLargePrinterAsFirst(){
        return machines.getLargePrinter(firstMachineID);
    }

    Machines.SmallPrinter getSmallPrinterAsFirst(){
        return machines.getSmallPrinter(firstMachineID);
    }

    Machines.BindingMachine getBindingMachineAsFirst(){
        return machines.getBindingMachine(firstMachineID);
    }

    Client.clientStates checkStateOfFirstClientInQueue(){
        return parentCoordinator.checkFirstClientInQueue().getClientState();
    }

    Boolean isSmallPrintPossible(){
        Boolean isPrintPossible = false;
        for(int i=0; i<machines.getSmallPrinterNum();i++)
            isPrintPossible = isPrintPossible || machines.getSmallPrinter(i).checkIfPrintPossible();
        return isPrintPossible;
    }

    Boolean isAnySmallFree(){
        Boolean anySmallFree= false;
        for(int i=0; i<machines.getSmallPrinterNum();i++)
            anySmallFree =  anySmallFree || !machines.getSmallPrinter(i).isBusy();
        return anySmallFree;
    }

    Boolean checkConditions(){
        switch(header){
            // AWARIE ////////////////////////////////////
            case PRZYBYCIE_AWARII_DUZA:
            case PRZYBYCIE_AWARII_MALA:
            case PRZYBYCIE_AWARII_BINDOWNICA:
                return this.isEqualToCurrentSimulationTime();
            case OBSLUGA_AWARII_DUZA_START:
                return (getLargePrinterAsFirst().isBreakdown()
                        && !getLargePrinterAsFirst().isBusy()
                        && isStaffAvailable());
            case OBSLUGA_AWARII_MALA_START:
                return (getSmallPrinterAsFirst().isBreakdown()
                        && !getSmallPrinterAsFirst().isBusy()
                        && isStaffAvailable());
            case OBSLUGA_AWARII_BINDOWNICA_START:
                return (getBindingMachineAsFirst().isBreakdown()
                        && !getBindingMachineAsFirst().isBusy()
                        && isStaffAvailable());
            case OBSLUGA_AWARII_DUZA_KONIEC:
            case OBSLUGA_AWARII_MALA_KONIEC:
            case OBSLUGA_AWARII_BINDOWNICA_KONIEC:
                return this.isEqualToCurrentSimulationTime();
            // WYMIANY ////////////////////////////////////
            case WYMIANA_PAPIERU_DUZA_START:
                return (getLargePrinterAsFirst().getPaperAmount() <
                        getLargePrinterAsFirst().getPaperCap()/ PAPER_AND_INK_EXCHANGE_THRESHOLD
                        && isStaffAvailable());
            case WYMIANA_PAPIERU_MALA_START:
                return (getSmallPrinterAsFirst().getPaperAmount() <
                        getSmallPrinterAsFirst().getPaperCap()/ PAPER_AND_INK_EXCHANGE_THRESHOLD
                        && isStaffAvailable());
            case WYMIANA_PAPIERU_DUZA_KONIEC:
            case WYMIANA_PAPIERU_MALA_KONIEC:
                return isEqualToCurrentSimulationTime();
            case WYMIANA_TUSZU_DUZA_START:
                return (getLargePrinterAsFirst().getInkAmount() <
                        getLargePrinterAsFirst().getCartridgeCap()/ PAPER_AND_INK_EXCHANGE_THRESHOLD
                        && isStaffAvailable());
            case WYMIANA_TUSZU_MALA_START:
                return (getSmallPrinterAsFirst().getInkAmount() <
                        getSmallPrinterAsFirst().getCartridgeCap()/ PAPER_AND_INK_EXCHANGE_THRESHOLD
                        && isStaffAvailable());
            case WYMIANA_TUSZU_DUZA_KONIEC:
            case WYMIANA_TUSZU_MALA_KONIEC:
                return isEqualToCurrentSimulationTime();
            // KLIENCI ////////////////////////////////////
            case PRZYBYCIE_KLIENTA_MALY_DRUK_BIND:
            /*case PRZYBYCIE_KLIENTA_MALY_DRUK_DUZY_DRUK:
            case PRZYBYCIE_ZAMOWIENIA_ELEKTRONICZNEGO:*/
                return isEqualToCurrentSimulationTime();
            /*case PRZYBYCIE_KLIENTA_ODB_ZAM:
                return (isEqualToCurrentSimulationTime() && parentCoordinator.getOrdersToPickUpNum()>0);*/
            case OBSLUGA_KLIENTA_MALY_DRUK_BIND_START:
                return (parentCoordinator.clientQueueSize()>0
                        && checkStateOfFirstClientInQueue()== Client.clientStates.SMALL_PRINT_BIND_IN_QUEUE
                        && isStaffAvailable() && !getBindingMachineAsFirst().isBreakdown()
                        && isSmallPrintPossible());
            case OBSLUGA_KLIENTA_MALY_DRUK_BIND_KONIEC:
                return isEqualToCurrentSimulationTime();
            case WYJSCIE_KLIENTA_MALY_DRUK_BIND_START:
                return parentCoordinator.hasAnyServicedClientState(Client.clientStates.SMALL_PRINT_BIND_HAPPY);
            case WYJSCIE_KLIENT_MALY_DRUK_BIND_KONIEC:
                return isEqualToCurrentSimulationTime();
            // DRUK+BIND ////////////////////////////////////
            case DRUKOWANIE_MALA_START://@TODO pozostale opcje (klienci)
                return (parentCoordinator.hasAnyServicedClientState(Client.clientStates.SMALL_PRINT_BIND_WAITING_SMALL_PRINT)
                            && isAnySmallFree());
            case DRUKOWANIE_MALA_KONIEC:
                return isEqualToCurrentSimulationTime();
            case BINDOWANIE_START:
                return (parentCoordinator.hasAnyServicedClientState(Client.clientStates.SMALL_PRINT_BIND_WAITING_BIND)
                            && !getBindingMachineAsFirst().isBusy());
            case BINDOWANIE_KONIEC:
                return isEqualToCurrentSimulationTime();
            default:
                System.out.println("Niedozwolony HEADER !!");
        }
            return false;
    }

    Integer getNewChangeStateTimeAfterBreakdown(){
        return new Integer(parentCoordinator.getCurrentSimulationTime() +
                getRandomGenerator().nextInt(NEXT_BREAKDOWN_TIME) + NEXT_BREAKDOWN_TIME);
    }

    Integer getNewChangeStateTimeForNextClientArrival(){
        return new Integer(parentCoordinator.getCurrentSimulationTime() +
                getRandomGenerator().nextInt(NEXT_CLIENT_TIME) + NEXT_CLIENT_TIME);
    }

    void setIDForFirstFreeSmallPrinter(){
        for(int i=0; i<machines.getSmallPrinterNum();i++)
             if(!machines.getSmallPrinter(i).isBusy()){
                 firstMachineID = i;
                 return;
             }
        return;
    }

    void setIDForFirstFreeBindingMachine(){
        for(int i=0; i<machines.getBinderNum();i++)
            if(!machines.getBindingMachine(i).isBusy()){
                firstMachineID = i;
                return;
            }
        return;
    }

    Activity runProcedure(){
        Integer newChangeStateTime;
        Machines.SmallPrinter smallPrinter;
        switch(header){
            // AWARIE ////////////////////////////////////
            case PRZYBYCIE_AWARII_DUZA:
                getLargePrinterAsFirst().changeBreakDown(true);
                return produceNextActivity(Headers.OBSLUGA_AWARII_DUZA_START);
            case PRZYBYCIE_AWARII_MALA:
                getSmallPrinterAsFirst().changeBreakDown(true);
                return produceNextActivity(Headers.OBSLUGA_AWARII_MALA_START);
            case PRZYBYCIE_AWARII_BINDOWNICA:
                getBindingMachineAsFirst().changeBreakDown(true);
                return produceNextActivity(Headers.OBSLUGA_AWARII_BINDOWNICA_START);
            case OBSLUGA_AWARII_DUZA_START:
                parentCoordinator.availableStaffNum--;
                getLargePrinterAsFirst().changeBreakDown(false);
                getLargePrinterAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        +getLargePrinterAsFirst().getBreakdownRepairTime());
                return produceNextActivity(Headers.OBSLUGA_AWARII_DUZA_KONIEC, newChangeStateTime);
            case OBSLUGA_AWARII_MALA_START:
                parentCoordinator.availableStaffNum--;
                getSmallPrinterAsFirst().changeBreakDown(false);
                getSmallPrinterAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        +getSmallPrinterAsFirst().getBreakdownRepairTime());
                return produceNextActivity(Headers.OBSLUGA_AWARII_MALA_KONIEC, newChangeStateTime);
            case OBSLUGA_AWARII_BINDOWNICA_START:
                parentCoordinator.availableStaffNum--;
                getBindingMachineAsFirst().changeBreakDown(false);
                getBindingMachineAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        +getBindingMachineAsFirst().getBreakdownRepairTime());
                return produceNextActivity(Headers.OBSLUGA_AWARII_BINDOWNICA_KONIEC, newChangeStateTime);
            case OBSLUGA_AWARII_DUZA_KONIEC:
                parentCoordinator.availableStaffNum++;
                getLargePrinterAsFirst().changeBusy(false);
                newChangeStateTime = getNewChangeStateTimeAfterBreakdown();
                return produceNextActivity(Headers.PRZYBYCIE_AWARII_DUZA, newChangeStateTime);
            case OBSLUGA_AWARII_MALA_KONIEC:
                parentCoordinator.availableStaffNum++;
                getSmallPrinterAsFirst().changeBusy(false);
                newChangeStateTime = getNewChangeStateTimeAfterBreakdown();
                return produceNextActivity(Headers.PRZYBYCIE_AWARII_MALA, newChangeStateTime);
            case OBSLUGA_AWARII_BINDOWNICA_KONIEC:
                parentCoordinator.availableStaffNum++;
                getBindingMachineAsFirst().changeBusy(false);
                newChangeStateTime = getNewChangeStateTimeAfterBreakdown();
                return produceNextActivity(Headers.PRZYBYCIE_AWARII_BINDOWNICA, newChangeStateTime);
            // WYMIANY ////////////////////////////////////
            case WYMIANA_PAPIERU_DUZA_START:
                parentCoordinator.availableStaffNum--;
                getLargePrinterAsFirst().setPaperAmount(
                        new Integer(getLargePrinterAsFirst().getPaperCap()));
                getLargePrinterAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        + getLargePrinterAsFirst().getPaperExchangeTime());
                return produceNextActivity(Headers.WYMIANA_PAPIERU_DUZA_KONIEC, newChangeStateTime, true);
            case WYMIANA_PAPIERU_MALA_START:
                parentCoordinator.availableStaffNum--;
                getSmallPrinterAsFirst().setPaperAmount(
                        new Integer(getSmallPrinterAsFirst().getPaperCap()));
                getSmallPrinterAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        + getSmallPrinterAsFirst().getPaperExchangeTime());
                return produceNextActivity(Headers.WYMIANA_PAPIERU_MALA_KONIEC, newChangeStateTime, true);
            case WYMIANA_TUSZU_DUZA_START:
                parentCoordinator.availableStaffNum--;
                getLargePrinterAsFirst().setInkAmount(
                        new Integer(getLargePrinterAsFirst().getCartridgeCap()));
                getLargePrinterAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        + getLargePrinterAsFirst().getInkExchangeTime());
                return produceNextActivity(Headers.WYMIANA_TUSZU_DUZA_KONIEC, newChangeStateTime, true);
            case WYMIANA_TUSZU_MALA_START:
                parentCoordinator.availableStaffNum--;
                getSmallPrinterAsFirst().setInkAmount(
                        new Integer(getSmallPrinterAsFirst().getCartridgeCap()));
                getSmallPrinterAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        + getSmallPrinterAsFirst().getInkExchangeTime());
                return produceNextActivity(Headers.WYMIANA_PAPIERU_MALA_KONIEC, newChangeStateTime, true);
            case WYMIANA_PAPIERU_DUZA_KONIEC:
            case WYMIANA_TUSZU_DUZA_KONIEC:
                parentCoordinator.availableStaffNum++;
                getLargePrinterAsFirst().changeBusy(false);
                return null;
            case WYMIANA_PAPIERU_MALA_KONIEC:
            case WYMIANA_TUSZU_MALA_KONIEC:
                parentCoordinator.availableStaffNum++;
                getSmallPrinterAsFirst().changeBusy(false);
                return null;
            // KLIENCI ////////////////////////////////////
            case PRZYBYCIE_KLIENTA_MALY_DRUK_BIND:
                parentCoordinator.addToClientQueue(client);
                client.setClientState(Client.clientStates.SMALL_PRINT_BIND_IN_QUEUE);
                newChangeStateTime = getNewChangeStateTimeForNextClientArrival();
                return produceNextActivity(newChangeStateTime);
            /*case PRZYBYCIE_KLIENTA_MALY_DRUK_DUZY_DRUK:
            case PRZYBYCIE_ZAMOWIENIA_ELEKTRONICZNEGO:
            case PRZYBYCIE_KLIENTA_ODB_ZAM:*/
            case OBSLUGA_KLIENTA_MALY_DRUK_BIND_START:
                parentCoordinator.availableStaffNum--;
                client = parentCoordinator.getFirstClientInQueue();
                parentCoordinator.addToServicedClients(client);
                return produceNextActivity(Headers.OBSLUGA_KLIENTA_MALY_DRUK_BIND_KONIEC,
                        parentCoordinator.getCurrentSimulationTime()+1, true);
            case OBSLUGA_KLIENTA_MALY_DRUK_BIND_KONIEC:
                client.setClientState(Client.clientStates.SMALL_PRINT_BIND_WAITING_SMALL_PRINT);
                client.setClientSmallPrintPages();
                client.setBindNum();
                return null;
            case WYJSCIE_KLIENTA_MALY_DRUK_BIND_START:
                return produceNextActivity(Headers.WYJSCIE_KLIENT_MALY_DRUK_BIND_KONIEC,
                        parentCoordinator.getCurrentSimulationTime()+1, true);
            case WYJSCIE_KLIENT_MALY_DRUK_BIND_KONIEC:
                parentCoordinator.availableStaffNum++;
                parentCoordinator.removeFromServicedClients(Client.clientStates.SMALL_PRINT_BIND_HAPPY);
                return null;
            // DRUK+BIND ////////////////////////////////////
            case DRUKOWANIE_MALA_START:
                setIDForFirstFreeSmallPrinter();
                getSmallPrinterAsFirst().changeBusy(true);
                client = parentCoordinator.getFirstServicedClient(Client.clientStates.SMALL_PRINT_BIND_WAITING_SMALL_PRINT);
                getSmallPrinterAsFirst().updateInkAmount(client.getSmallPrintPages());
                getSmallPrinterAsFirst().updatePaperAmount(client.getSmallPrintPages());
                newChangeStateTime = parentCoordinator.getCurrentSimulationTime() + getSmallPrinterAsFirst().getPrintingTime(client.getSmallPrintPages());
                return produceNextActivity(Headers.DRUKOWANIE_MALA_KONIEC,
                        newChangeStateTime, true);
            case DRUKOWANIE_MALA_KONIEC:
                getSmallPrinterAsFirst().changeBusy(false);
                client.makeStateTransition();
                return null;
            case BINDOWANIE_START:
                setIDForFirstFreeBindingMachine();
                getBindingMachineAsFirst().changeBusy(true);
                client = parentCoordinator.getFirstServicedClient(Client.clientStates.SMALL_PRINT_BIND_WAITING_BIND);
                newChangeStateTime = parentCoordinator.getCurrentSimulationTime()
                        + getBindingMachineAsFirst().getBindingTime(client.getBindNum());
                return produceNextActivity(Headers.BINDOWANIE_KONIEC,
                        newChangeStateTime, true);
            case BINDOWANIE_KONIEC:
                getBindingMachineAsFirst().changeBusy(false);
                client.setClientState(Client.clientStates.SMALL_PRINT_BIND_HAPPY);
                return null;
            default:
                System.out.println("Niedozwolony HEADER !!");
        }
        return null;
    }

    Activity produceNextActivity(Headers newHeader){
        return new Activity(newHeader, stateChangeTime, client,
                machines, firstMachineID, secondMachineID,
                parentCoordinator, removable);
    }


    Activity produceNextActivity(Integer newChangeStateTime){
        return new Activity(header, newChangeStateTime, client,
                 machines, firstMachineID, secondMachineID,
                parentCoordinator, removable);
    }

    Activity produceNextActivity(Headers newHeader, Integer newChangeStateTime){
        return new Activity(newHeader, newChangeStateTime, client,
                machines, firstMachineID, secondMachineID,
                parentCoordinator, removable);
    }

    Activity produceNextActivity(Headers newHeader, Integer newChangeStateTime, Boolean newRemovable){
        return new Activity(newHeader, newChangeStateTime, client,
                machines, firstMachineID, secondMachineID,
                parentCoordinator, newRemovable);
    }

    void reportActivity(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Activity Type: ").append(header.toString()).append(" ").append(firstMachineID);
        System.out.println(stringBuilder.toString());
    }

}
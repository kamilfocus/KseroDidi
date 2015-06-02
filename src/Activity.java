import java.util.Random;

/**
 * Created by KAMIL on 2015-06-02.
 */
public class Activity{

    Coordinator parentCoordinator;

    headers header;
    Integer stateChangeTime;
    Integer firstMachineID;
    Integer secondMachineID;

    Client client;
    Machines machines;

    static final int NEXT_BREAKDOWN_TIME = 100;

    public enum headers{
        // AWARIE ////////////////////////////////////
        PRZYBYCIE_AWARII_DUZA,
        PRZYBYCIE_AWARII_MALA,
        PRZYBYCIE_AWARII_BINDOWNICA,
        OBSLUGA_AWARII_DUZA_START,
        OBSLUGA_AWARII_DUZA_KONIEC,
        OBSLUGA_AWARII_MALA_START,
        OBSLUGA_AWARII_MALA_KONIEC,
        OBSLUGA_AWARII_BINDOWNICA_START,
        OBSLUGA_AWARII_BINDOWNICA_KONIEC
    }

    Activity(headers header, Integer stateChangeTime, Client client,
             Machines machines, Integer firstMachineID, Integer secondMachineID){

        this.header = header;
        this.stateChangeTime = stateChangeTime;
        this.client = client;
        this.machines = machines;
        this.firstMachineID = firstMachineID;
        this.secondMachineID = secondMachineID;

    }

    Integer getStateChangeTime(){
        return stateChangeTime;
    }

    Random getRandomGenerator(){
        return machines.getRandomGenerator();
    }

    Boolean isEqualToCurrentSimulationTime(){
        return (stateChangeTime == parentCoordinator.getCurrentSimulationTime()) ? true : false;
    }

    Boolean isStaffAvailable(){
        return (parentCoordinator.getAvailableStaffNumber()>0) ? true : false;
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

    Boolean checkConditions(){
        switch(header){
            // AWARIE ////////////////////////////////////
            case PRZYBYCIE_AWARII_DUZA:
            case PRZYBYCIE_AWARII_MALA:
            case PRZYBYCIE_AWARII_BINDOWNICA:
                this.isEqualToCurrentSimulationTime();
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
                this.isEqualToCurrentSimulationTime();
            default:
                System.out.println("Niedozwolony HEADER !!");
        }
            return false;
    }

    Integer getNewChangeStateTimeAfterBreakdown(){
        return new Integer(parentCoordinator.getCurrentSimulationTime() +
                getRandomGenerator().nextInt(NEXT_BREAKDOWN_TIME) + NEXT_BREAKDOWN_TIME);
    }

    Activity runProcedure(){
        Integer newChangeStateTime;
        switch(header){
            // AWARIE ////////////////////////////////////
            case PRZYBYCIE_AWARII_DUZA:
                getLargePrinterAsFirst().changeBreakDown(true);
                newChangeStateTime = getNewChangeStateTimeAfterBreakdown();
                return produceNextActivity(newChangeStateTime);
            case PRZYBYCIE_AWARII_MALA:
                getSmallPrinterAsFirst().changeBreakDown(true);
                newChangeStateTime = getNewChangeStateTimeAfterBreakdown();
                return produceNextActivity(newChangeStateTime);
            case PRZYBYCIE_AWARII_BINDOWNICA:
                getBindingMachineAsFirst().changeBreakDown(true);
                newChangeStateTime = getNewChangeStateTimeAfterBreakdown();
                return produceNextActivity(newChangeStateTime);
            case OBSLUGA_AWARII_DUZA_START:
                parentCoordinator.availableStaffNum--;
                getLargePrinterAsFirst().changeBreakDown(false);
                getLargePrinterAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        +getLargePrinterAsFirst().getBreakdownRepairTime());
                return produceNextActivity(headers.OBSLUGA_AWARII_DUZA_KONIEC, newChangeStateTime);
            case OBSLUGA_AWARII_MALA_START:
                parentCoordinator.availableStaffNum--;
                getSmallPrinterAsFirst().changeBreakDown(false);
                getSmallPrinterAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        +getSmallPrinterAsFirst().getBreakdownRepairTime());
                return produceNextActivity(headers.OBSLUGA_AWARII_MALA_KONIEC, newChangeStateTime);
            case OBSLUGA_AWARII_BINDOWNICA_START:
                parentCoordinator.availableStaffNum--;
                getBindingMachineAsFirst().changeBreakDown(false);
                getBindingMachineAsFirst().changeBusy(true);
                newChangeStateTime = new Integer(parentCoordinator.getCurrentSimulationTime()
                        +getBindingMachineAsFirst().getBreakdownRepairTime());
                return produceNextActivity(headers.OBSLUGA_AWARII_BINDOWNICA_KONIEC, newChangeStateTime);
            case OBSLUGA_AWARII_DUZA_KONIEC:
                parentCoordinator.availableStaffNum++;
                getLargePrinterAsFirst().changeBusy(false);
                return null;
            case OBSLUGA_AWARII_MALA_KONIEC:
                parentCoordinator.availableStaffNum++;
                getSmallPrinterAsFirst().changeBusy(false);
                return null;
            case OBSLUGA_AWARII_BINDOWNICA_KONIEC:
                parentCoordinator.availableStaffNum++;
                getBindingMachineAsFirst().changeBusy(false);
                return null;
            default:
                System.out.println("Niedozwolony HEADER !!");
        }
        return null;
    }

    Activity produceNextActivity(Integer newChangeStateTime){
        return new Activity(header, newChangeStateTime, client,
                 machines, firstMachineID, secondMachineID);
    }

    Activity produceNextActivity(headers newHeader, Integer newChangeStateTime){
        return new Activity(newHeader, newChangeStateTime, client,
                machines, firstMachineID, secondMachineID);
    }

    void reportActivity(){
        System.out.println("Activity Type: " + header.toString());
    }

}
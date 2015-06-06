/**
 * Created by KAMIL on 2015-06-01.
 */

import java.io.*;
import java.util.*;

public class Simulation {

    static public void main(String [] args){
        System.out.println("Hello Didi !");

        Integer totalSimulationTime = new Integer(1000);
        Integer smallPrinterNum = new Integer(3);
        Integer largePrinterNum = new Integer(1);
        Integer binderNum = new Integer(1);
        Integer staffNum = new Integer(4);

        Coordinator coordinator = new Coordinator(
                totalSimulationTime, smallPrinterNum, largePrinterNum, binderNum, staffNum);

        coordinator.startSimulation();
        coordinator.reportSimulationSummary();
    }
}

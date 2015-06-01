/**
 * Created by KAMIL on 2015-06-01.
 */
public class Simulation {

    static public void main(String [] args){
        System.out.println("Hello Didi !");
        Integer totalSimulationTime = new Integer(1000);
        Coordinator coordinator = new Coordinator(totalSimulationTime);
        coordinator.startSimulation();
    }
}

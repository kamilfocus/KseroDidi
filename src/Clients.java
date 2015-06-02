import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

/**
 * Created by KAMIL on 2015-06-02.
 */
public class Clients {

    Random generator;
    static final int queueCapacity = 100;

    BlockingQueue<Client> clientsQueue;
    BlockingQueue<ElectronicOrder> ordersQueue;

    Clients(){
        generator = new Random();

        clientsQueue = new ArrayBlockingQueue<Client>(queueCapacity);
        ordersQueue = new ArrayBlockingQueue<ElectronicOrder>(queueCapacity);
    }

    public enum clientTypes{
        SMALL_PRINT_BIND,
        SMALL_PRINT_LARGE_PRINT,
        RECEIVE_ORDER
    }

    public class Client{
        clientTypes clientType;
        Client(clientTypes clientType){
            this.clientType = clientType;
        }
    }

    public class ElectronicOrder{

        Integer pagesToPrint;

        ElectronicOrder(){
            pagesToPrint = new Integer(1+generator.nextInt(100));
        }
    }
}

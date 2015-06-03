
/**
 * Created by KAMIL on 2015-06-02.
 */

public class Client {

        public enum clientTypes{
            SMALL_PRINT_BIND,
            SMALL_PRINT_LARGE_PRINT,
            RECEIVE_ORDER,
            ELECTRONIC_ORDER,
            BREAKDOWN_LARGE,
            BREAKDOWN_SMALL,
            BREAKDOWN_BIND
        }

        clientTypes clientType;
        Client(clientTypes clientType){
            this.clientType = clientType;
        }
}




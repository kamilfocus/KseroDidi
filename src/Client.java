
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
            BREAKDOWN_BIND,
            PAPER_EXCHANGE_LARGE,
            PAPER_EXCHANGE_SMALL,
            INK_EXCHANGE_LARGE,
            INK_EXCHANGE_SMALL
        }

        clientTypes clientType;
        Client(clientTypes clientType){
            this.clientType = clientType;
        }
}




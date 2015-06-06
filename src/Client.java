import java.util.Random;

/**
 * Created by KAMIL on 2015-06-02.
 */

public class Client {

        Random r;
        static final int PRINT_TO_BIND_RATIO = 50;

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
            INK_EXCHANGE_SMALL,
            NOT_DETERMINED
        }

        public enum clientStates{

            SMALL_PRINT_BIND_IN_QUEUE,
            SMALL_PRINT_BIND_OUT_QUEUE,
            SMALL_PRINT_BIND_WAITING_SMALL_PRINT,
            SMALL_PRINT_BIND_IN_SMALL_PRINT,
            SMALL_PRINT_BIND_IN_BIND,
            SMALL_PRINT_BIND_WAITING_BIND,
            SMALL_PRINT_BIND_HAPPY,
            SMALL_PRINT_BIND_EXIT,

            SMALL_PRINT_LARGE_PRINT_IN_QUEUE,
            SMALL_PRINT_LARGE_PRINT_OUT_QUEUE,
            SMALL_PRINT_LARGE_PRINT_WAITING_SMALL_PRINT,
            SMALL_PRINT_LARGE_PRINT_IN_SMALL_PRINT,
            SMALL_PRINT_LARGE_PRINT_WAITING_LARGE_PRINT,
            SMALL_PRINT_LARGE_PRINT_IN_LARGE_PRINT,
            SMALL_PRINT_LARGE_PRINT_HAPPY,
            SMALL_PRINT_LARGE_PRINT_EXIT,

            RECEIVE_ORDER_IN_QUEUE,
            RECEIVE_ORDER_OUT_QUEUE,
            RECEIVE_ORDER_HAPPY,

            ELECTRONIC_ORDER_IN_QUEUE,
            ELECTRONIC_ORDER_OUT_QUEUE,
            ELECTRONIC_ORDER_WAITING_SMALL_PRINT,
            ELECTRONIC_ORDER_IN_SMALL_PRINT,
            ELECTRONIC_ORDER_HAPPY,
            ELECTRONIC_ORDER_ON_SHELF
        }

        clientTypes clientType;
        clientStates clientState;
        Integer smallPrintPages;
        Integer largePrintPages;
        Integer bindNum;

        Client(clientTypes clientType){
            this.clientType = clientType;
            this.r = new Random();
        }

        public void setClientState(clientStates clientState) {
            this.clientState = clientState;
        }

        public clientStates getClientState(){
            return clientState;
        }

        void setClientSmallPrintPages(){
            smallPrintPages = 10 + r.nextInt(10);
        }

        Integer getSmallPrintPages(){
            return smallPrintPages;
        }

        void setClientLargePrintPages(){largePrintPages = 3 + r.nextInt(3);}

        Integer getLargePrintPages(){ return largePrintPages;}

        void setBindNum(){
            bindNum = 1 + smallPrintPages%PRINT_TO_BIND_RATIO;
        }

        Integer getBindNum(){
            return bindNum;
        }

        void makeStateTransition(){
            //@TODO More Transitions to put
            switch(clientState){

                case SMALL_PRINT_BIND_IN_QUEUE:
                    clientState = clientStates.SMALL_PRINT_BIND_OUT_QUEUE;
                    break;
                case SMALL_PRINT_BIND_OUT_QUEUE:
                    clientState = clientStates.SMALL_PRINT_BIND_WAITING_SMALL_PRINT;
                    break;
                case SMALL_PRINT_BIND_WAITING_SMALL_PRINT:
                    clientState = clientStates.SMALL_PRINT_BIND_IN_SMALL_PRINT;
                    break;
                case SMALL_PRINT_BIND_IN_SMALL_PRINT:
                    clientState = clientStates.SMALL_PRINT_BIND_WAITING_BIND;
                    break;
                case SMALL_PRINT_BIND_WAITING_BIND:
                    clientState = clientStates.SMALL_PRINT_BIND_IN_BIND;
                    break;
                case SMALL_PRINT_BIND_IN_BIND:
                    clientState = clientStates.SMALL_PRINT_BIND_HAPPY;
                    break;
                case SMALL_PRINT_BIND_HAPPY:
                    clientState = clientStates.SMALL_PRINT_BIND_EXIT;
                    break;

                case SMALL_PRINT_LARGE_PRINT_IN_QUEUE:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_OUT_QUEUE;
                    break;
                case SMALL_PRINT_LARGE_PRINT_OUT_QUEUE:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_WAITING_SMALL_PRINT;
                    break;
                case SMALL_PRINT_LARGE_PRINT_WAITING_SMALL_PRINT:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_IN_SMALL_PRINT;
                    break;
                case SMALL_PRINT_LARGE_PRINT_IN_SMALL_PRINT:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_WAITING_LARGE_PRINT;
                    break;
                case SMALL_PRINT_LARGE_PRINT_WAITING_LARGE_PRINT:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_IN_LARGE_PRINT;
                    break;
                case SMALL_PRINT_LARGE_PRINT_IN_LARGE_PRINT:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_HAPPY;
                    break;
                case SMALL_PRINT_LARGE_PRINT_HAPPY:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_EXIT;
                    break;

                case RECEIVE_ORDER_IN_QUEUE:
                    clientState = clientStates.RECEIVE_ORDER_OUT_QUEUE;
                    break;
                case RECEIVE_ORDER_OUT_QUEUE:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_HAPPY;
                    break;
                case RECEIVE_ORDER_HAPPY:
                    clientState = clientStates.SMALL_PRINT_LARGE_PRINT_EXIT;
                    break;

                case ELECTRONIC_ORDER_IN_QUEUE:
                    clientState = clientStates.ELECTRONIC_ORDER_OUT_QUEUE;
                    break;
                case ELECTRONIC_ORDER_OUT_QUEUE:
                    clientState = clientStates.ELECTRONIC_ORDER_WAITING_SMALL_PRINT;
                    break;
                case ELECTRONIC_ORDER_WAITING_SMALL_PRINT:
                    clientState = clientStates.ELECTRONIC_ORDER_IN_SMALL_PRINT;
                    break;
                case ELECTRONIC_ORDER_IN_SMALL_PRINT:
                    clientState = clientStates.ELECTRONIC_ORDER_HAPPY;
                    break;
                case ELECTRONIC_ORDER_HAPPY:
                    clientState = clientStates.ELECTRONIC_ORDER_ON_SHELF;
                    break;

                default:
                    System.out.println("Incorrect State in makeStateTransition()");
                    System.out.println(clientState);
            }
        }
}




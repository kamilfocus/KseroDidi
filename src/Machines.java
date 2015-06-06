import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by KAMIL on 2015-06-02.
 */
public class Machines {

    Integer smallPrinterNum;
    Integer largePrinterNum;
    Integer binderNum;

    Random generator;

    static final int PAPER_AND_INK_EXCHANGE_THRESHOLD = 5;
    static final int SECS_TO_MIN = 60;

    List<SmallPrinter> smallPrinters;
    List<LargePrinter> largePrinters;
    List<BindingMachine> bindingMachines;

    Machines(Integer smallPrinterNum, Integer largePrinterNum, Integer binderNum){

        generator = new Random();

        this.smallPrinterNum=smallPrinterNum;
        this.largePrinterNum=largePrinterNum;
        this.binderNum=binderNum;

        smallPrinters = new ArrayList<SmallPrinter>();
        largePrinters = new ArrayList<LargePrinter>();
        bindingMachines = new ArrayList<BindingMachine>();

        for(int i=0; i<smallPrinterNum; i++)
            smallPrinters.add(new SmallPrinter());

        for(int i=0; i<largePrinterNum; i++)
            largePrinters.add(new LargePrinter());

        for(int i=0; i<binderNum; i++)
            bindingMachines.add(new BindingMachine());
    }

    Random getRandomGenerator(){
        return generator;
    }

    public Integer getSmallPrinterNum() {
        return smallPrinterNum;
    }

    public Integer getLargePrinterNum(){
        return largePrinterNum;
    }

    public Integer getBinderNum(){
        return  binderNum;
    }

    public LargePrinter getLargePrinter(int position){
        return largePrinters.get(position);
    }

    public SmallPrinter getSmallPrinter(int position){
        return smallPrinters.get(position);
    }

    public BindingMachine getBindingMachine(int position){
        return bindingMachines.get(position);
    }


    public class SmallPrinter extends Printer{

        SmallPrinter(){

            cartridgeCap = new Integer(500);
            paperCap = new Integer(200);
            printingTime = new Integer(5+generator.nextInt(6));
            inkConsumption = new Integer(1+generator.nextInt(1));
            paperConsumption = new Integer(1);
            breakdownRepairTime = new Integer(5 + generator.nextInt(56));

            inkAmount = new Integer(cartridgeCap);
            paperAmount = new Integer(paperCap);
        }

        Integer getBreakdownRepairTime(){
            breakdownRepairTime = (5 + generator.nextInt(56));
            return breakdownRepairTime;
        }

        Integer getPrintingTime(Integer pagesToPrint){
            printingTime = 5+generator.nextInt(6);
            return (printingTime*pagesToPrint/SECS_TO_MIN + 1);
        }

    }

    public class LargePrinter extends Printer{

        LargePrinter(){

            cartridgeCap = new Integer(1000);
            paperCap = new Integer(50);
            printingTime = new Integer(20+generator.nextInt(11));
            inkConsumption = new Integer(5+generator.nextInt(6));
            paperConsumption = new Integer(1);
            breakdownRepairTime = new Integer(60 + generator.nextInt(31));

            inkAmount = new Integer(cartridgeCap);
            paperAmount = new Integer(paperCap);
        }

        Integer getBreakdownRepairTime(){
            breakdownRepairTime = (60 + generator.nextInt(31));
            return breakdownRepairTime;
        }

        Integer getPrintingTime(Integer pagesToPrint){
            printingTime = 20+generator.nextInt(11);
            return (printingTime*pagesToPrint/SECS_TO_MIN + 1);
        }

    }

    public class Printer extends Machine{

        Integer inkAmount;
        Integer paperAmount;

        Integer cartridgeCap;
        Integer paperCap;
        Integer printingTime;
        Integer inkConsumption;
        Integer paperConsumption;
        Integer breakdownRepairTime;
        Integer inkExchangeTime;
        Integer paperExchangeTime;

        Printer(){
            inkExchangeTime = new Integer(1+generator.nextInt(5));
            paperExchangeTime = new Integer(1+generator.nextInt(1));
        }

        Integer getPaperCap(){
            return paperCap;
        }

        Integer getPaperAmount(){
            return paperAmount;
        }

        Integer getInkAmount(){
            return inkAmount;
        }

        Integer getCartridgeCap(){
            return cartridgeCap;
        }

        Integer getInkExchangeTime(){
            inkExchangeTime = 1+generator.nextInt(5);
            return inkExchangeTime;
        }

        Integer getPaperExchangeTime(){
            paperExchangeTime = 1+generator.nextInt(1);
            return paperExchangeTime;
        }

        void setPaperAmount(Integer paperAmount){
            this.paperAmount = paperAmount;
        }

        void setInkAmount(Integer inkAmount){
            this.inkAmount = inkAmount;
        }

        Boolean checkIfPrintPossible(){
            return !isBreakdown() && inkAmount>cartridgeCap/PAPER_AND_INK_EXCHANGE_THRESHOLD
                    && paperAmount>paperCap/PAPER_AND_INK_EXCHANGE_THRESHOLD;
        }

        void updateInkAmount(Integer pagesToPrint){
            inkAmount = inkAmount - pagesToPrint*inkConsumption;
        }

        void updatePaperAmount(Integer pagesToPrint){
            paperAmount = paperAmount - pagesToPrint*paperConsumption;
        }
    }

    public class BindingMachine extends Machine{

        Integer bindingTime;
        Integer breakdownRepairTime;

        BindingMachine(){

            bindingTime = new Integer(1+generator.nextInt(1));
            breakdownRepairTime = new Integer(5 + generator.nextInt(26));

        }

        Integer getBreakdownRepairTime(){
            breakdownRepairTime = (5 + generator.nextInt(26));
            return breakdownRepairTime;
        }

        Integer getBindingTime(Integer blocksToBind){
            bindingTime = 1+generator.nextInt(1);
            return (bindingTime *blocksToBind + 1);
        }

    }

    public class Machine{

        Boolean busy;
        Boolean breakdown;

        Integer totalBusyTime;

        Machine(){
            busy = new Boolean(false);
            breakdown = new Boolean(false);

            totalBusyTime = new Integer(0);
        }

        Boolean isBreakdown(){
            return breakdown;
        }

        Boolean isBusy(){
            return busy;
        }

        void changeBusy(Boolean newState){
            busy = newState;
        }

        void changeBreakDown(Boolean newState){
            breakdown = newState;
        }

        void calculateBusyTime(Integer oldTime, Integer newTime){
            if(isBusy())
                totalBusyTime += newTime - oldTime;
        }

        Integer getTotalBusyTime(){
            return totalBusyTime;
        }

    }

}

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

    public class SmallPrinter{

        Boolean busy;
        Boolean breakdown;
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

        SmallPrinter(){

            cartridgeCap = new Integer(500);
            paperCap = new Integer(200);
            printingTime = new Integer(5+generator.nextInt(6));
            inkConsumption = new Integer(1+generator.nextInt(1));
            paperConsumption = new Integer(1);
            breakdownRepairTime = new Integer(5 + generator.nextInt(56));
            inkExchangeTime = new Integer(1+generator.nextInt(5));
            paperExchangeTime = new Integer(1+generator.nextInt(1));

            busy = new Boolean(false);
            breakdown = new Boolean(false);
            inkAmount = new Integer(cartridgeCap);
            paperAmount = new Integer(paperCap);
        }
    }

    public class LargePrinter{

        Boolean busy;
        Boolean breakdown;
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

        LargePrinter(){

            cartridgeCap = new Integer(1000);
            paperCap = new Integer(50);
            printingTime = new Integer(20+generator.nextInt(11));
            inkConsumption = new Integer(5+generator.nextInt(6));
            paperConsumption = new Integer(1);
            breakdownRepairTime = new Integer(60 + generator.nextInt(31));
            inkExchangeTime = new Integer(1+generator.nextInt(5));
            paperExchangeTime = new Integer(1+generator.nextInt(1));

            busy = new Boolean(false);
            breakdown = new Boolean(false);
            inkAmount = new Integer(cartridgeCap);
            paperAmount = new Integer(paperCap);
        }
    }

    public class BindingMachine{

        Boolean busy;
        Boolean breakdown;

        Integer bindingTime;
        Integer breakdownRepairTime;


        BindingMachine(){

            bindingTime = new Integer(1+generator.nextInt(1));
            breakdownRepairTime = new Integer(5 + generator.nextInt(26));

            busy = new Boolean(false);
            breakdown = new Boolean(false);
        }
    }

}

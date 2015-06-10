import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by Renia on 2015-06-04.
 */
public class Gui extends JFrame implements ActionListener
{

    JButton startButton, statisticsButton, clearButton;
    JLabel titleLabel, smallPages, largePages, bind, clients, ordersQueue, clientsQueue;
    JLabel small, large, binder, staff, time;
    AddTime addTime;
    Statistics statistics;
    JTextArea resultTArea;
    JScrollPane resultScroll;

    Integer totalSimulationTime;
    Integer smallPrinterNum;
    Integer largePrinterNum;
    Integer binderNum;
    Integer staffNum;


    public Gui()
    {
        setTitle("Punkt Ksero");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800,600);
        setTitle("Punkt Ksero");
        setLocationRelativeTo(null);

        setLayout(new MigLayout("","[grow]","[grow]"));

        titleLabel = new JLabel("Symulacja Punktu Ksero");
        titleLabel.setFont(new Font("Serif", Font.ITALIC, 20));
        add(titleLabel,"span 3, wrap");

        resultTArea = new JTextArea();
        resultScroll = new JScrollPane(resultTArea);
        resultTArea.setEditable(false);
        resultTArea.setToolTipText("Zapis wykonanych dzialan");

        add(resultScroll, "span 2 3, width 650, height 400");

        startButton = new JButton("Start");
        startButton.setToolTipText("Rozpoczecie symulacji");
        add(startButton,"width 200, height 25, wrap");
        startButton.addActionListener(this);

        clearButton = new JButton("Wyczysc");
        clearButton.setToolTipText("Wyczyszczenie wynikow symulacji");
        add(clearButton,"width 200, height 25, wrap");
        clearButton.addActionListener(this);

        statisticsButton = new JButton("Podsumowanie");
        statisticsButton.setToolTipText("Podsumowanie wynikow symulacji");
        add(statisticsButton,"width 200, height 25, wrap");
        statisticsButton.addActionListener(this);

        smallPages = new JLabel("Ilosc wydrukowanych stron na malej drukarce: 0");
        largePages = new JLabel("Ilosc wydrukowanych stron na duzej drukarce: 0");
        bind = new JLabel("Ilosc zbindowanych stron: 0");
        clients = new JLabel("Ilosc obsluzoych klientow: 0");
        clientsQueue = new JLabel("Ilosc klientow w kolejce: 0");
        ordersQueue = new JLabel("Ilosc zamowien w kolejce: 0");

        small = new JLabel("Ilosc malych drukarek: 0");
        small.setFont(new Font("Serif", Font.ITALIC, 16));
        large = new JLabel("Ilosc duzych drukarek: 0");
        large.setFont(new Font("Serif", Font.ITALIC, 16));
        binder = new JLabel("Ilosc bindownic to: 0");
        binder.setFont(new Font("Serif", Font.ITALIC, 16));
        staff = new JLabel("Ilosc pracownikow to: 0");
        staff.setFont(new Font("Serif", Font.ITALIC, 16));
        time = new JLabel("Czas trwania symulacji to: 0");
        time.setFont(new Font("Serif", Font.ITALIC, 16));

        smallPages.setFont(new Font("Serif", Font.ITALIC, 16));
        largePages.setFont(new Font("Serif", Font.ITALIC, 16));
        bind.setFont(new Font("Serif", Font.ITALIC, 16));
        clients.setFont(new Font("Serif", Font.ITALIC, 16));
        clientsQueue.setFont(new Font("Serif", Font.ITALIC, 16));
        ordersQueue.setFont(new Font("Serif", Font.ITALIC, 16));

        add(small);
        add(smallPages, "wrap");
        add(large);
        add(largePages, "wrap");
        add(binder);
        add(bind, "wrap");
        add(staff);
        add(clientsQueue, "wrap");
        add(time);
        add(ordersQueue);
    }

    public static void main(String[] args)
    {
        Gui gui = new Gui();
        gui.setVisible(true);
    }
    //
    @Override
    public void actionPerformed(ActionEvent ev)
    {
        Object source = ev.getSource();
        if (source==startButton)
        {
            if (addTime==null) addTime = new AddTime(this);
            addTime.setVisible(true);
            addTime.setFocus();

            if (addTime.isOk()){
                totalSimulationTime = new Integer(addTime.returnTime());
                smallPrinterNum = new Integer(addTime.returnSmall());
                largePrinterNum = new Integer(addTime.returnLarge());
                binderNum = new Integer(addTime.returnBind());
                staffNum = new Integer(addTime.returnStaff());

                small.setText("Ilosc malych drukarek: " + smallPrinterNum);
                large.setText("Ilosc duzych drukarek: " + largePrinterNum);
                binder.setText("Ilosc bindownic: " + binderNum);
                staff.setText("Ilosc pracownikow: " + staffNum);
                time.setText("Czas trwania symulacji to: " + totalSimulationTime);

                Coordinator coordinator = new Coordinator(
                        totalSimulationTime, smallPrinterNum, largePrinterNum, binderNum, staffNum, this);
                resultTArea.setText("WYNIK DZIALANIA SYMULACJI:");
                coordinator.startSimulation();
                coordinator.printResults(this);

                if (statistics==null) statistics = new Statistics(this);
                statistics.statsSmall.setText("Zajetosc malych drukarek: \n");
                statistics.statsLarge.setText("Zajetosc duzych drukarek: \n");
                statistics.statsBind.setText("Zajetosc bindownic: \n");
                statistics.statsStaff.setText("Ilosc wolnych pracownikow: \n");
                coordinator.printStats(this);
                coordinator.printStaff(this);
            }
        }
        else if(source==statisticsButton)
        {
            if (statistics==null) statistics = new Statistics(this);
            statistics.setVisible(true);
        }

        else if(source==clearButton)
        {
            resultTArea.setText("Wyczyszczono wyniki symulacji!");
            smallPages.setText("Ilosc wydrukowanych stron na malej drukarce: 0");
            largePages.setText("Ilosc wydrukowanych stron na duzej drukarce: 0");
            bind.setText("Ilosc zbindowanych stron: 0");
            clients.setText("Ilosc obsluzoych klientow: 0");
            clientsQueue.setText("Ilosc klientow w kolejce: 0");
            ordersQueue.setText("Ilosc zamowien w kolejce: 0");
            statistics.statsSmall.setText("Zajetosc malych drukarek: \n");
            statistics.statsLarge.setText("Zajetosc duzych drukarek: \n");
            statistics.statsBind.setText("Zajetosc bindownic: \n");
            statistics.statsStaff.setText("Ilosc wolnych pracownikow: \n");
            small.setText("Ilosc malych drukarek: 0");
            large.setText("Ilosc duzych drukarek: 0");
            binder.setText("Ilosc bindownic: 0");
            staff.setText("Ilosc pracownikow: 0");
            time.setText("Czas trwania symulacji to: 0");
        }
    }
}

class Statistics extends JDialog {
    JTextArea statsLarge, statsSmall, statsBind, statsStaff;
    JScrollPane  resultScroll1,resultScroll2, resultScroll3, resultScroll4;

    Statistics(JFrame owner){
        super(owner, "Podsumowanie", true);
        setSize(400,600);
        setLocationRelativeTo(null);
        setLayout(new MigLayout("","[grow]","[grow]"));
        statsSmall = new JTextArea();
        resultScroll1 = new JScrollPane(statsSmall);
        statsSmall.setFont(new Font("Serif", Font.ITALIC, 16));
        statsSmall.append("Zajetosc malych drukarek: \n");
        statsSmall.setEditable(false);
        statsSmall.setToolTipText("Pokazuje jaka czesc czasu symulacji poszczegolne male drukarki byly zajete");
        statsLarge = new JTextArea();
        resultScroll2 = new JScrollPane(statsLarge);
        statsLarge.setFont(new Font("Serif", Font.ITALIC, 16));
        statsLarge.append("Zajetosc duzych drukarek: \n");
        statsLarge.setEditable(false);
        statsLarge.setToolTipText("Pokazuje jaka czesc czasu symulacji poszczegolne duze drukarki byly zajete");
        statsBind = new JTextArea();
        resultScroll3 = new JScrollPane(statsBind);
        statsBind.setFont(new Font("Serif", Font.ITALIC, 16));
        statsBind.append("Zajestosc bindownic: \n");
        statsBind.setEditable(false);
        statsBind.setToolTipText("Pokazuje jaka czesc czasu symulacji poszczegolne bindownice byly zajete");
        statsStaff = new JTextArea();
        statsStaff.setToolTipText("Pokazuje ilosc dostepnych pracownikow podczas trwania symulacji");
        resultScroll4 = new JScrollPane(statsStaff);
        statsStaff.setFont(new Font("Serif", Font.ITALIC, 16));
        statsStaff.append("Ilosc wolnych pracownikow: \n");
        statsSmall.setEditable(false);

        add(resultScroll1,"width 400, height 150, wrap");
        add(resultScroll2,"width 400, height 150, wrap");
        add(resultScroll3,"width 400, height 150, wrap");
        add(resultScroll4,"width 400, height 150, wrap");
    }
}

class AddTime extends JDialog implements  ChangeListener,ActionListener
{
    JLabel timeLabel, smallLabel, largeLabel, bindLabel, staffLabel;
    JSlider timeSlider, smallSlider, largeSlider, bindSlider, staffSlider;
    JButton okButton, cancelButton;
    Integer time = 1000, small = 3, large = 1, bind = 1, staff = 2;
    Boolean okTime = false;

    public AddTime(JFrame owner)
    {
        super(owner, "Ustawienie parametrow symulacji", true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setSize(700,350);
        setLocationRelativeTo(null);
        setLayout(new MigLayout("","[grow]","[grow]"));

        timeLabel = new JLabel("Czas symulacji to: " + time);
        timeLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        timeSlider = new JSlider(0, 100000, time);
        timeSlider.setMajorTickSpacing(50000);
        timeSlider.setMinorTickSpacing(10000);
        timeSlider.setPaintLabels(true);
        timeSlider.setPaintTicks(true);
        timeSlider.addChangeListener(this);
        timeSlider.setToolTipText("Czas symulacji");

        smallLabel = new JLabel("Ilosc malych drukarek to: " + small);
        smallLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        smallSlider = new JSlider(0, 10, small);
        smallSlider.setMajorTickSpacing(5);
        smallSlider.setMinorTickSpacing(1);
        smallSlider.setPaintLabels(true);
        smallSlider.setPaintTicks(true);
        smallSlider.addChangeListener(this);
        smallSlider.setToolTipText("Male drukarki");


        largeLabel = new JLabel("Ilosc duzych drukarek to: " + large);
        largeLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        largeSlider = new JSlider(0, 10, large);
        largeSlider.setMajorTickSpacing(5);
        largeSlider.setMinorTickSpacing(1);
        largeSlider.setPaintLabels(true);
        largeSlider.setPaintTicks(true);
        largeSlider.addChangeListener(this);
        largeSlider.setEnabled(false);
        largeSlider.setToolTipText("Duze drukarki");

        bindLabel = new JLabel("Ilosc bindownic to: " + bind);
        bindLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        bindSlider = new JSlider(0, 10, bind);
        bindSlider.setMajorTickSpacing(5);
        bindSlider.setMinorTickSpacing(1);
        bindSlider.setPaintLabels(true);
        bindSlider.setPaintTicks(true);
        bindSlider.addChangeListener(this);
        bindSlider.setEnabled(false);
        bindSlider.setToolTipText("Bindownice");

        staffLabel = new JLabel("Ilosc pracownikow to: " + staff);
        staffLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        staffSlider = new JSlider(0, 10, staff);
        staffSlider.setMajorTickSpacing(5);
        staffSlider.setMinorTickSpacing(1);
        staffSlider.setPaintLabels(true);
        staffSlider.setPaintTicks(true);
        staffSlider.addChangeListener(this);
        staffSlider.setToolTipText("Pracownicy");

        cancelButton = new JButton("Anuluj");
        okButton = new JButton("Ok");
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        add(timeSlider,"width 200, height 25");
        add(timeLabel,"width 200, height 25, wrap");
        add(smallSlider,"width 200, height 25");
        add(smallLabel,"width 200, height 25, wrap");
        add(largeSlider,"width 200, height 25");
        add(largeLabel,"width 200, height 25, wrap");
        add(bindSlider,"width 200, height 25");
        add(bindLabel,"width 200, height 25");
        add(okButton,"width 100, height 25, wrap");
        add(staffSlider,"width 200, height 25");
        add(staffLabel,"width 200, height 25");
        add(cancelButton,"width 100, height 25");
    }

    public boolean isOk()
    {
        return okTime;
    }
    public int returnTime()
    {
        return time;
    }
    public int returnSmall() {return small;}
    public int returnLarge() {return large;}
    public int returnBind() {return bind;}
    public int returnStaff() {return staff;}
    public void setFocus() {timeSlider.requestFocusInWindow();}

    @Override
    public void actionPerformed(ActionEvent eventTime)
    {
        Object x = eventTime.getSource();
        if(x==okButton)
            okTime = true;
        else if (x==cancelButton) {
            okTime = false;
            small = 3;
            large = 1;
            bind = 1;
            staff = 2;
            time = 1000;
        }
        setVisible(false);
    }

    @Override
    public void stateChanged(ChangeEvent listener_time)
    {
        time = timeSlider.getValue();
        timeLabel.setText("Czas symulacji to: " + time);

        small = smallSlider.getValue();
        smallLabel.setText("Ilosc malych drukarek to: " + small);

        large = largeSlider.getValue();
        largeLabel.setText("Ilosc duzych drukarek to: " + large);

        bind = bindSlider.getValue();
        bindLabel.setText("Ilosc bindownic to: " + bind);

        staff = staffSlider.getValue();
        staffLabel.setText("Ilosc pracownikow to: " +staff);
    }
}

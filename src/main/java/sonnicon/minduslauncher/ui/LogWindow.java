package sonnicon.minduslauncher.ui;

import sonnicon.minduslauncher.type.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class LogWindow extends Window{


    public LogWindow(String name, Process process){
        super(name);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        JTextArea log = new JTextArea();
        log.setEditable(false);
        JScrollPane logPane = new JScrollPane(log);
        logPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(logPane);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(0, 3));

        JButton buttonClear = new JButton("Clear");
        buttonClear.addActionListener(l -> {
            log.selectAll();
            log.replaceSelection("");
        });
        panelButtons.add(buttonClear);

        JButton buttonKill = new JButton("Kill");
        buttonKill.addActionListener(l -> process.destroyForcibly());
        panelButtons.add(buttonKill);

        JButton buttonClose = new JButton("Cancel");
        buttonClose.addActionListener(l -> frame.dispose());
        panelButtons.add(buttonClose);

        frame.add(BorderLayout.SOUTH, panelButtons);

        Scanner s = new Scanner(process.getInputStream());
        new Thread(() -> {
            while(process.isAlive() && frame.isDisplayable()){
                if(s.hasNextLine()){
                    log.append(s.nextLine() + "\n");
                    log.setCaretPosition(log.getDocument().getLength());
                }else{
                    try{
                        Thread.sleep(500);
                    }catch(InterruptedException ignored){}
                }
            }
            if(!process.isAlive()){
                buttonKill.setEnabled(false);
            }
        }).start();

    }

    @Override
    protected int defaultWidth(){
        return 500;
    }

    @Override
    protected int defaultHeight(){
        return 700;
    }
}

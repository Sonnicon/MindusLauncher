package sonnicon.minduslauncher.ui;

import sonnicon.minduslauncher.type.FrameWindow;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogWindow extends FrameWindow{

    public LogWindow(String name, Process process){
        super(name);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        JTextPane log = new JTextPane();
        StyledDocument document = log.getStyledDocument();
        JScrollPane logPane = new JScrollPane(log);
        logPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(logPane);
        log.setAutoscrolls(true);

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

        Style style = new StyleContext().addStyle("log", null);
        BufferedReader inp = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        new Thread(() -> {
            while(process.isAlive() && frame.isDisplayable()){
                try{
                    boolean b = inp.ready();
                    if(b || err.ready()){
                        StyleConstants.setForeground(style, b ? Color.BLACK : Color.RED);
                        document.insertString(document.getLength(), (b ? inp.readLine() : err.readLine()) + "\n", style);
                        log.setCaretPosition(document.getLength());
                    }else{
                        Thread.sleep(500);
                    }
                }catch(InterruptedException | IOException | BadLocationException ignored){}
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

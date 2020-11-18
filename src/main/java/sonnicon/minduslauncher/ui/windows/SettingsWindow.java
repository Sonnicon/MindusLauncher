package sonnicon.minduslauncher.ui.windows;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.ui.ModalWindow;
import sonnicon.minduslauncher.type.config.Setting;

import javax.swing.*;
import java.awt.*;

public class SettingsWindow extends ModalWindow{
    protected boolean reload = false;

    public SettingsWindow(){
        super("Settings");
        frame.setLayout(new BorderLayout());


        JPanel panelSettings = new JPanel();
        JScrollPane pane = new JScrollPane(panelSettings);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelSettings.setLayout(new GridLayout(Vars.config.settings().length, 2));
        frame.add(pane);

        for(Setting<?> s : Vars.config.settings()){
            s.label(panelSettings);
            s.component(panelSettings);
        }

        //bottom
        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(1, 2));

        panelButtons.add(runnableButton("OK", () -> {
            if(reload){
                JOptionPane.showMessageDialog(frame, "Restart MindusLauncher to apply some settings.");
            }
            frame.setVisible(false);
            Vars.config.write();
        }));

        panelButtons.add(runnableButton("About", () -> {
            Vars.aboutWindow.show();
        }));

        panelButtons.add(runnableButton("Cancel", () ->
            frame.setVisible(false)
        ));

        frame.add(BorderLayout.SOUTH, panelButtons);
    }

    @Override
    protected int defaultWidth() {
        return 400;
    }

    @Override
    protected int defaultHeight() {
        return 70 + Vars.config.settings().length * 30;
    }


    JButton runnableButton(String text, Runnable onPress){
        JButton button = new JButton(text);
        button.addActionListener(e -> onPress.run());
        return button;
    }

    public void reload(){
        reload = true;
    }
}

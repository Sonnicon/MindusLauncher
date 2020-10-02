package sonnicon.minduslauncher.ui;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SettingsWindow extends Window{

    public SettingsWindow(){
        super("Settings");
        frame.setLayout(new BorderLayout());

        JPanel panelSettings = new JPanel();
        panelSettings.setLayout(new GridLayout(1, 2));
        frame.add(panelSettings);

        JLabel themeLabel = new JLabel("Theme");
        panelSettings.add(themeLabel);

        UIManager.LookAndFeelInfo[] looksAndFeels = javax.swing.UIManager.getInstalledLookAndFeels();

        String[] themes;
        {
            ArrayList<String> themeNames = new ArrayList<>();
            for (UIManager.LookAndFeelInfo lafi : looksAndFeels) {
                themeNames.add(lafi.getName());
            }
            themes = themeNames.toArray(new String[]{});
        }
        JComboBox<String> themeCombobox = new JComboBox<>(themes);
        panelSettings.add(themeCombobox);
        themeCombobox.setSelectedItem(UIManager.getLookAndFeel().getName());
        themeCombobox.addActionListener(
                e -> Vars.config.setTheme(UIManager.getInstalledLookAndFeels()[themeCombobox.getSelectedIndex()].getClassName()));


        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(0, 1));

        panelButtons.add(runnableButton("OK", () ->
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
        return 100;
    }


    JButton runnableButton(String text, Runnable onPress){
        JButton button = new JButton(text);
        button.addActionListener(e -> onPress.run());
        return button;
    }

    public void show(){
        frame.setVisible(true);
    }
}

package sonnicon.minduslauncher.ui;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.files.Config;
import sonnicon.minduslauncher.type.Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SettingsWindow extends Window{

    public SettingsWindow(){
        super("Settings");
        frame.setLayout(new BorderLayout());

        JPanel panelSettings = new JPanel();
        panelSettings.setLayout(new GridLayout(3, 2));
        frame.add(panelSettings);

        //Theme
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

        //Open Log
        JLabel openlogLabel = new JLabel("Open Log");
        panelSettings.add(openlogLabel);

        JCheckBox openlogCheckbox = new JCheckBox();
        openlogCheckbox.setSelected(Vars.config.getOpenLog());
        openlogCheckbox.addActionListener(l -> Vars.config.setOpenLog(openlogCheckbox.isSelected()));
        panelSettings.add(openlogCheckbox);

        //Select Previous
        JLabel selectpreviousLabel = new JLabel("Select Latest On Startup");
        panelSettings.add(selectpreviousLabel);

        JCheckBox selectpreviousCheckbox = new JCheckBox();
        selectpreviousCheckbox.setSelected(Vars.config.getSelectPrevious());
        selectpreviousCheckbox.addActionListener(l -> Vars.config.setSelectPrevious(selectpreviousCheckbox.isSelected()));
        panelSettings.add(selectpreviousCheckbox);

        //bottom
        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(1, 2));

        panelButtons.add(runnableButton("OK", () -> {
                frame.setVisible(false);
                Config.write();
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
        return 160;
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

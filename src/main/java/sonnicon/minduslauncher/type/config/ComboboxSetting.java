package sonnicon.minduslauncher.type.config;

import javax.swing.*;
import java.awt.*;

public class ComboboxSetting extends Setting<String>{

    public ComboboxSetting(String key, String value, String label){
        super(key, value, label);
    }

    public String[] values(){
        return new String[]{""};
    }

    @Override
    public void component(Container container){
        JComboBox<String> combobox = new JComboBox<>(values());
        container.add(combobox);
        combobox.getModel().setSelectedItem(value);
        combobox.addActionListener(e -> setValue(combobox.getSelectedItem()));
    }
}

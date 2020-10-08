package sonnicon.minduslauncher.type.config;

import javax.swing.*;
import java.awt.*;

public class CheckboxSetting extends Setting<Boolean>{

    public CheckboxSetting(String key, Boolean value, String label){
        super(key, value, label);
    }

    @Override
    public void component(Container container){
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(getValue());
        checkBox.addActionListener(l -> setValue(checkBox.isSelected()));
        container.add(checkBox);
    }
}

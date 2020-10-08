package sonnicon.minduslauncher.type.config;

import javax.swing.*;
import java.awt.*;

public abstract class Setting<T> extends Configuration<T>{
    protected String label;

    public Setting(String key, T value, String label){
        super(key, value);
        this.label = label;
    }

    public abstract void component(Container container);

    public void label(Container container){
        JLabel jLabel = new JLabel(label);
        container.add(jLabel);
    }
}

package sonnicon.minduslauncher.ui;

import sonnicon.minduslauncher.core.Vars;

import javax.swing.*;

public abstract class ModalWindow extends Window<JDialog>{

    public ModalWindow(String name){
        this(Vars.launcherWindow.getFrame(), name);
    }

    public ModalWindow(JFrame parent, String name){
        super();
        frame = new JDialog(parent, name, true);
        applySize();
    }
}

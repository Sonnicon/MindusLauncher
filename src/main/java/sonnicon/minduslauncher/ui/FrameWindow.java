package sonnicon.minduslauncher.ui;

import javax.swing.*;

public abstract class FrameWindow extends Window<JFrame>{
    public FrameWindow(String name){
        super();
        frame = new JFrame(name);
        applySize();
    }
}

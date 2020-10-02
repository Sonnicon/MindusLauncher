package sonnicon.minduslauncher.type;

import javax.swing.*;
import java.util.ArrayList;

public abstract class Window {
    protected JFrame frame;

    protected static ArrayList<Window> windows = new ArrayList<>();

    public Window(String name){
        windows.add(this);
        frame = new JFrame(name);
        applySize();
    }

    protected abstract int defaultWidth();

    protected abstract int defaultHeight();

    protected void applySize(){
        frame.setSize(defaultWidth(), defaultHeight());
    }

    protected void update(){
        SwingUtilities.updateComponentTreeUI(frame);
        frame.pack();
        applySize();
    }

    public static void updateAll(){
        for(Window w : windows){
            w.update();
        }
    }
}

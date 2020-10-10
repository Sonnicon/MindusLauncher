package sonnicon.minduslauncher.type;

import javax.swing.*;
import java.util.ArrayList;

public abstract class Window<T extends java.awt.Window>{
    protected T frame;

    protected static ArrayList<Window<?>> windows = new ArrayList<>();

    public Window(){
        windows.add(this);
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
        for(Window<?> w : windows){
            w.update();
        }
    }
}

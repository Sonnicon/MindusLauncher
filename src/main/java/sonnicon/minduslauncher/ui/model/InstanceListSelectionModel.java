package sonnicon.minduslauncher.ui.model;

import sonnicon.minduslauncher.core.Vars;
import javax.swing.*;

public class InstanceListSelectionModel extends DefaultListSelectionModel{
    public InstanceListSelectionModel(){
        super();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addListSelectionListener(e -> Vars.launcherWindow.setEditButtonsEnabled(true));
    }

    @Override
    public void clearSelection(){
    }

    @Override
    public void removeSelectionInterval(int index0, int index1){
    }


}

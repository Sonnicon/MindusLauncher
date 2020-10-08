package sonnicon.minduslauncher.ui.model;

import sonnicon.minduslauncher.core.Vars;
import javax.swing.*;

public class InstanceListSelectionModel extends DefaultListSelectionModel{
    public InstanceListSelectionModel(){
        super();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) return;
            Vars.launcherWindow.setEditButtonsEnabled(true);
            if((boolean) Vars.config.get("selectPrevious")){
                Vars.config.set("previous", Vars.launcherWindow.getSelected().file.getName());
                Vars.config.write();
            }
        });
    }

    @Override
    public void clearSelection(){
    }

    @Override
    public void removeSelectionInterval(int index0, int index1){
    }
}

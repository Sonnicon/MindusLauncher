package sonnicon.minduslauncher.ui.windows;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Instance;
import sonnicon.minduslauncher.ui.ModalWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.logging.Logger;

public class EditWindow extends ModalWindow{

    public final JTable tableEdit;
    public Instance target;

    public EditWindow(){
        super("Edit Instance");
        frame.setLayout(new BorderLayout());

        Object[][] rows = new Object[Instance.fields.length][2];
        for(int i = 0; i < rows.length; i++){
            rows[i] = new Object[]{Instance.fields[i].getAnnotation(Instance.InstanceEditable.class).displayName(), ""};
        }

        tableEdit = new JTable(rows, new String[]{"Name", "Value"});
        tableEdit.getTableHeader().setReorderingAllowed(false);
        tableEdit.setColumnSelectionAllowed(false);
        JScrollPane paneInstance = new JScrollPane(tableEdit);
        paneInstance.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(BorderLayout.CENTER, paneInstance);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(0, 3));

        panelButtons.add(runnableButton("Apply", () -> {
            TableModel editModel = tableEdit.getModel();

            for(int i = 0; i < Instance.fields.length; i++){
                try{
                    Instance.fields[i].set(target, editModel.getValueAt(i, 1));
                }catch(IllegalAccessException ignored){}
            }

            TableModel instanceModel = Vars.launcherWindow.tableInstance.getModel();
            int index = Vars.instances.indexOf(target);
            instanceModel.setValueAt(target.name, index, 0);
            instanceModel.setValueAt(target.version, index, 1);

            frame.setVisible(false);
            Vars.instanceIO.saveInstanceJson(target);
        }));

        panelButtons.add(runnableButton("Delete", () -> {
            if(JOptionPane.showConfirmDialog(frame, "Are you sure you want to permanently delete instance '" + target.name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                target.delete();
                target = null;
                frame.setVisible(false);
                Vars.launcherWindow.setEditButtonsEnabled(false);
            }
        }));
        panelButtons.add(runnableButton("Cancel", () -> frame.setVisible(false)));

        frame.add(BorderLayout.SOUTH, panelButtons);
    }

    @Override
    protected int defaultWidth() {
        return 400;
    }

    @Override
    protected int defaultHeight() {
        return 400;
    }


    JButton runnableButton(String text, Runnable onPress){
        JButton button = new JButton(text);
        button.addActionListener(e -> onPress.run());
        return button;
    }

    public void showFor(Instance target){
        this.target = target;
        TableModel editModel = tableEdit.getModel();
        for(int i = 0; i < Instance.fields.length; i++){
            try{
                editModel.setValueAt(Instance.fields[i].get(target), i, 1);
            }catch(IllegalAccessException ignored){}
        }
        frame.setVisible(true);
    }
}

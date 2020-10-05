package sonnicon.minduslauncher.ui;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Instance;
import sonnicon.minduslauncher.type.Window;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class EditWindow extends Window{

    public final JTable tableEdit;
    public Instance target;

    public EditWindow(){
        super("Edit Instance");
        frame.setLayout(new BorderLayout());

        tableEdit = new JTable(new Object[][]{{"Name", ""}, {"Version", ""}, {"Cmd Args", ""}, {"Mindustry Args", ""}}, new String[]{"Name", "Value"});
        tableEdit.getTableHeader().setReorderingAllowed(false);
        tableEdit.setColumnSelectionAllowed(false);
        JScrollPane paneInstance = new JScrollPane(tableEdit);
        paneInstance.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(BorderLayout.CENTER, paneInstance);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(0, 3));

        panelButtons.add(runnableButton("Apply", () -> {
            //todo clean
            TableModel editModel = tableEdit.getModel();

            target.name = (String) editModel.getValueAt(0, 1);
            target.version = (String) editModel.getValueAt(1, 1);
            target.cmdArgs = (String) editModel.getValueAt(2, 1);
            target.mindustryArgs = (String) editModel.getValueAt(3, 1);

            TableModel instanceModel = Vars.launcherWindow.tableInstance.getModel();
            int index = Vars.instances.indexOf(target);
            instanceModel.setValueAt(target.name, index, 0);
            instanceModel.setValueAt(target.version, index, 1);

            frame.setVisible(false);
            Vars.instanceIO.saveInstanceJson(target);
        }));
        panelButtons.add(runnableButton("Delete", () -> {
            if(JOptionPane.showConfirmDialog(frame, "Are you sure you want to permanently delete instance '" + target.name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                int index = Vars.instances.indexOf(target);
                ((DefaultTableModel) Vars.launcherWindow.tableInstance.getModel()).removeRow(index);
                Vars.instances.remove(index);
                try{
                    Files.walk(target.file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::deleteOnExit);
                    target.file.deleteOnExit();
                }catch(IOException ex){
                    ex.printStackTrace();
                }
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

    public void showFor(Instance i){
        target = i;
        TableModel editModel = tableEdit.getModel();
        editModel.setValueAt(i.name, 0, 1);
        editModel.setValueAt(i.version, 1, 1);
        editModel.setValueAt(i.cmdArgs, 2, 1);
        editModel.setValueAt(i.mindustryArgs, 3, 1);
        frame.setVisible(true);
    }
}

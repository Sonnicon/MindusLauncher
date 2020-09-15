package sonnicon.minduslauncher.ui;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Instance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class EditWindow{
    private final JFrame frame;

    public final JTable tableEdit;
    public Instance target;

    public EditWindow(){
        frame = new JFrame("Edit Instance");
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        tableEdit = new JTable(new Object[][]{{"Name", "null"}, {"Version", "null"}}, new String[]{"Name", "Value"});
        tableEdit.getTableHeader().setReorderingAllowed(false);
        tableEdit.setColumnSelectionAllowed(false);
        JScrollPane paneInstance = new JScrollPane(tableEdit);
        paneInstance.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(BorderLayout.CENTER, paneInstance);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(0, 3));

        panelButtons.add(runnableButton("Apply", () -> {
            target.name = (String) tableEdit.getModel().getValueAt(0, 1);
            target.version = (String) tableEdit.getModel().getValueAt(1, 1);
            int index = Vars.instances.indexOf(target);
            Vars.launcherWindow.tableInstance.getModel().setValueAt(target.name, index, 0);
            Vars.launcherWindow.tableInstance.getModel().setValueAt(target.version, index, 1);
            frame.setVisible(false);
            Vars.instanceIO.saveInstanceJson(target);
        }));
        panelButtons.add(runnableButton("Delete", () -> {
            int index = Vars.instances.indexOf(target);
            ((DefaultTableModel)Vars.launcherWindow.tableInstance.getModel()).removeRow(index);
            Vars.instances.remove(index);
            try{
                Files.walk(target.file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::deleteOnExit);
                target.file.deleteOnExit();
            }catch(IOException ex){
                ex.printStackTrace();
            }
            frame.setVisible(false);
            Vars.launcherWindow.setEditButtonsEnabled(false);
        }));
        panelButtons.add(runnableButton("Cancel", () -> {
            frame.setVisible(false);
        }));

        frame.add(BorderLayout.SOUTH, panelButtons);
    }

    JButton runnableButton(String text, Runnable onPress){
        JButton button = new JButton(text);
        button.addActionListener(e -> onPress.run());
        return button;
    }

    public void showFor(Instance i){
        target = i;
        tableEdit.getModel().setValueAt(i.name, 0, 1);
        tableEdit.getModel().setValueAt(i.version, 1, 1);
        frame.setVisible(true);
    }
}

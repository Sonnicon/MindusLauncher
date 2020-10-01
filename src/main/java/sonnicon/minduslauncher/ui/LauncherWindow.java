package sonnicon.minduslauncher.ui;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Instance;
import sonnicon.minduslauncher.ui.component.UneditableTable;
import sonnicon.minduslauncher.ui.model.InstanceListSelectionModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LauncherWindow{
    private final JFrame frame;

    public final UneditableTable tableInstance;

    public LauncherWindow(){
        frame = new JFrame("MindusLauncher");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        tableInstance = new UneditableTable(new String[]{"Name", "Version"});
        tableInstance.setColumnSelectionAllowed(false);
        tableInstance.setSelectionModel(new InstanceListSelectionModel());
        JScrollPane paneInstance = new JScrollPane(tableInstance);
        paneInstance.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(BorderLayout.CENTER, paneInstance);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(0, 1));

        panelButtons.add(runnableButton("Launch", () -> {
            Instance selected = getSelected();
            try{
                Runtime.getRuntime().exec("java " + selected.cmdArgs + " -cp \"" + Vars.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "\" sonnicon.minduslauncher.core.MindustryLauncher \"" + selected.jar.toPath() + "\" " + selected.mindustryArgs);
            }catch(IOException e){
                e.printStackTrace();
            }
        }, true));
        panelButtons.add(runnableButton("Update", () -> {
            //todo
        }, true));
        panelButtons.add(runnableButton("Edit", () -> {
            Vars.editWindow.showFor(getSelected());
        }, true));
        panelButtons.add(new JSeparator());

        panelButtons.add(runnableButton("Mods", () -> {
            //todo
        }, true));
        panelButtons.add(runnableButton("Loadermods", () -> {
            //todo
        }, true));
        panelButtons.add(runnableButton("Saves", () -> {
            //todo
        }, true));
        panelButtons.add(runnableButton("Maps", () -> {
            //todo
        }, true));
        panelButtons.add(new JSeparator());

        panelButtons.add(runnableButton("Add Official", () -> {
            Vars.officialWindow.show();
        }, false));
        panelButtons.add(runnableButton("Add Jar", () -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileFilter(){
                @Override
                public boolean accept(File f){
                    return f.getPath().endsWith(".jar");
                }

                @Override
                public String getDescription(){
                    return ".jar";
                }
            });
            if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
                new Instance(fileChooser.getSelectedFile());
            }
        }, false));
        panelButtons.add(runnableButton("Add Jar URL", () -> {
            String url = JOptionPane.showInputDialog("Enter JAR download URL:");
            if(url != null && !url.isEmpty()) Instance.instanceFromURL(url);
        }, false));
        panelButtons.add(new JSeparator());

        panelButtons.add(runnableButton("Settings", () -> {
            //todo
        }, false));

        frame.add(BorderLayout.EAST, panelButtons);
    }

    ArrayList<JButton> editButtons = new ArrayList<>();

    JButton runnableButton(String text, Runnable onPress, boolean editButton){
        JButton button = new JButton(text);
        button.addActionListener(e -> onPress.run());
        if(editButton){
            editButtons.add(button);
            button.setEnabled(false);
        }
        return button;
    }

    Instance getSelected(){
        return tableInstance.getSelectedRow() == -1 ? null : Vars.instances.get(tableInstance.getSelectedRow());
    }

    public void setEditButtonsEnabled(boolean enabled){
        for(JButton b : editButtons){
            b.setEnabled(enabled);
        }
    }

    public void show(){
        frame.setVisible(true);
    }
}

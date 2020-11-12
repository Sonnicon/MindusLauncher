package sonnicon.minduslauncher.ui.windows;

import com.google.gson.internal.LinkedTreeMap;
import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.files.FileIO;
import sonnicon.minduslauncher.ui.FrameWindow;
import sonnicon.minduslauncher.type.Instance;
import sonnicon.minduslauncher.ui.component.UneditableTable;
import sonnicon.minduslauncher.ui.model.InstanceListSelectionModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Logger;

public class LauncherWindow extends FrameWindow{
    public final UneditableTable tableInstance;

    private final ArrayList<AbstractButton> editButtons = new ArrayList<>();

    public LauncherWindow(){
        super("MindusLauncher");
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

        panelButtons.add(runnableButton("Launch", () -> getSelected().launch(), true));
        panelButtons.add(runnableButton("Launch Clean", () -> getSelected().launch(true), true));
        panelButtons.add(runnableButton("Edit", () -> Vars.editWindow.showFor(getSelected()), true));
        panelButtons.add(new JSeparator());

        panelButtons.add(runnableButton("Add Official", () -> Vars.officialWindow.show(), false));
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
        if((boolean) Vars.config.get("compileSource")){
            panelButtons.add(runnableButton("Add Source ZIP", () -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileFilter(){
                    @Override
                    public boolean accept(File f){
                        return f.getPath().endsWith(".zip");
                    }

                    @Override
                    public String getDescription(){
                        return ".zip";
                    }
                });
                if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
                    File target = new File(Vars.tempDir, fileChooser.getSelectedFile().getName());
                    try{
                        Files.copy(fileChooser.getSelectedFile().toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    FileIO.compileMindustry(target);
                }
            }, false));
            panelButtons.add(runnableButton("Add Source URL", () -> {
                String url = JOptionPane.showInputDialog("Enter source download URL:");
                if(url != null && !url.isEmpty()){
                    FileIO.compileMindustry(FileIO.fileFromURL(url));
                }
            }, false));
        }

        panelButtons.add(new JSeparator());

        JButton buttonMods = runnableButton("Mods", () -> Vars.modsWindow.show(), false);
        if(FileIO.getDatadir() == null || !FileIO.getDatadir().exists()){
            buttonMods.setEnabled(false);
        }
        panelButtons.add(buttonMods);

        if(Desktop.isDesktopSupported()) {
            JPopupMenu folderMenu = new JPopupMenu();
            for(String s : new String[]{"./", "Maps", "Saves", "Schematics", "Mods"})
                folderMenu.add(openFolderButton(s));
            folderMenu.add(openInstanceFolderButton("Instance", ""));
            folderMenu.add(openInstanceFolderButton("Loader Mods", "loadermods"));

            JButton folderButton = new JButton("Open Folder");
            folderButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    folderMenu.show(folderButton, e.getX(), e.getY());
                }
            });
            panelButtons.add(folderButton);
        }

        panelButtons.add(runnableButton("Settings", () -> Vars.settingsWindow.show(), false));

        frame.add(BorderLayout.EAST, panelButtons);

        if((boolean) Vars.config.get("popupLatestTag")){
            try{
                HttpURLConnection con = (HttpURLConnection) new URL("https://api.github.com/repos/Anuken/Mindustry/releases?per_page=1").openConnection();
                con.setRequestMethod("GET");
                con.connect();
                BufferedReader json = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //todo excluder
                ArrayList data = Vars.gson.fromJson(json, ArrayList.class);
                String tag = (String) ((LinkedTreeMap) data.get(0)).get("tag_name");
                if(!tag.equals(Vars.config.get("latestTag"))){
                    int option = JOptionPane.showOptionDialog(frame,
                            "Mindustry has released tag '" + tag + "'.",
                            "New Mindustry Version",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new String[]{"OK", "Remind Me Later"},
                            "OK");

                    if(option == 0){
                        Vars.config.set("latestTag", tag);
                        Vars.config.write();
                    }
                }
            }catch(Exception ex){
                Logger.getLogger(getClass().getName()).warning(ex.toString());
                JOptionPane.showMessageDialog(frame, "Error fetching latest release.\n" + ex.getMessage());
            }
        }
    }

    @Override
    protected int defaultWidth() {
        return 700;
    }

    @Override
    protected int defaultHeight() {
        return 350;
    }

    JButton runnableButton(String text, Runnable onPress, boolean editButton){
        JButton button = new JButton(text);
        button.addActionListener(e -> onPress.run());
        if(editButton){
            editButtons.add(button);
            button.setEnabled(false);
        }
        return button;
    }

    public Instance getSelected(){
        return tableInstance.getSelectedRow() == -1 ? null : Vars.instances.get(tableInstance.getSelectedRow());
    }

    JMenuItem openFolderButton(String name){
        JMenuItem button = new JMenuItem(name);
        button.addActionListener(a -> desktopOpenData(name));
        if(FileIO.getDatadir() == null || !FileIO.getDatadir().exists()){
            button.setEnabled(false);
        }
        return button;
    }

    JMenuItem openInstanceFolderButton(String name, String childName){
        JMenuItem button = new JMenuItem(name);
        button.addActionListener(a -> {
            File f = new File(getSelected().file, childName);
            f.mkdirs();
            desktopOpen(f);
        });
        editButtons.add(button);
        button.setEnabled(false);
        return button;
    }

    private void desktopOpenData(String name){
        desktopOpen(new File(FileIO.getDatadir(), name.toLowerCase()));
    }

    private void desktopOpen(File dir) {
        try {
            Desktop.getDesktop().open(dir);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).warning(ex.toString());
            JOptionPane.showMessageDialog(frame, "Error opening directory.\n" + ex.getMessage());
        }
    }

    public void setEditButtonsEnabled(boolean enabled){
        for(AbstractButton b : editButtons){
            b.setEnabled(enabled);
        }
    }

    public JFrame getFrame(){
        return frame;
    }
}

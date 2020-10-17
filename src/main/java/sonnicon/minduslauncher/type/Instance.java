package sonnicon.minduslauncher.type;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.files.FileIO;
import sonnicon.minduslauncher.ui.windows.LogWindow;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.logging.Logger;

public class Instance{
    public String name;
    public String version;
    public String cmdArgs = "";
    public String mindustryArgs = "";
    public File jar;
    public transient File file;

    public Instance(){
        Vars.instances.add(this);
    }

    public Instance(File f){
        this();
        file = Vars.fileIO.createInstanceDir(f.getName());
        try{
            Files.copy(f.toPath(), new File(file, f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException ex){
            Logger.getLogger(getClass().getName()).warning(ex.toString());
            return;
        }
        jar = new File(file, f.getName());
        name = file.getName();
        version = Vars.fileIO.readGameVersion(f);
        f.deleteOnExit();
        Vars.instanceIO.saveInstanceJson(this);
        addToTable();
    }

    public void init(File file){
        this.file = file;
        if(Vars.loaded) addToTable();
    }

    public static Instance instanceFromURL(String url){
        return new Instance(Objects.requireNonNull(FileIO.fileFromURL(url)));
    }

    public void addToTable(){
        if(Vars.loadUI){
            ((DefaultTableModel) Vars.launcherWindow.tableInstance.getModel()).addRow(new Object[]{name, version});
            if(Vars.loaded || ((boolean) Vars.config.get("selectPrevious") && Vars.config.get("previous").equals(file.getName()))){
                select();
            }
        }
    }

    private void select(){
        int row = Vars.launcherWindow.tableInstance.getRowCount() - 1;
        Vars.launcherWindow.tableInstance.setRowSelectionInterval(row, row);
        Vars.launcherWindow.setEditButtonsEnabled(true);
    }

    public void launch(){
        try{
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java",
                    cmdArgs,
                    "-cp",
                    Vars.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
                    "sonnicon.minduslauncher.core.MindustryLauncher",
                    jar.getAbsolutePath(),
                    mindustryArgs);
            builder.directory(file);

            Process process = builder.start();
            if((boolean) Vars.config.get("openLog") && Vars.loadUI)
                new LogWindow(name, process);
        }catch(IOException ex){
            Logger.getLogger(getClass().getName()).warning(ex.toString());
        }
    }
}

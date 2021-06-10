package sonnicon.minduslauncher.type;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.files.FileIO;
import sonnicon.minduslauncher.ui.windows.LogWindow;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Logger;

public class Instance{
    public @InstanceEditable(displayName = "Name") String name;
    public @InstanceEditable(displayName = "Version") String version;
    public @InstanceEditable(displayName = "Cmd Args") String cmdArgs = "";
    public @InstanceEditable(displayName = "Mindustry Args") String mindustryArgs = "";
    public File jar;
    public transient File file;

    public static final Field[] fields = Arrays.stream(Instance.class.getDeclaredFields()).filter(field -> field.isAnnotationPresent(InstanceEditable.class)).toArray(Field[]::new);

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
        return new Instance(FileIO.fileFromURL(url));
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

    public void delete(){
        int index = Vars.instances.indexOf(this);
        if(Vars.loadUI) ((DefaultTableModel) Vars.launcherWindow.tableInstance.getModel()).removeRow(index);
        Vars.instances.remove(index);
        try{
            Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::deleteOnExit);
            file.deleteOnExit();
        }catch(IOException ex){
            Logger.getLogger(getClass().getName()).warning(ex.toString());
        }
    }

    public void launch(){
        launch(false);
    }

    public void launch(boolean clean){
        try{
            ProcessBuilder builder = new ProcessBuilder();
            ArrayList<String> command = new ArrayList<>();
            command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
            if(cmdArgs.length() > 0){
                command.add(cmdArgs);
            }
            if(clean){
                command.add("-jar");
            }else{
                command.add("-cp");
                command.add(Vars.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                command.add("sonnicon.minduslauncher.core.MindustryLauncher");
            }
            command.add(jar.getPath());
            if(mindustryArgs.length() > 0){
                command.add(mindustryArgs);
            }
            builder.command(command);
            builder.directory(file);

            Process process = builder.start();
            if((boolean) Vars.config.get("openLog") && Vars.loadUI)
                new LogWindow(name, process);
        }catch(IOException ex){
            Logger.getLogger(getClass().getName()).warning(ex.toString());
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface InstanceEditable{
        String displayName() default "unknown";
    }
}

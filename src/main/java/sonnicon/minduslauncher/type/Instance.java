package sonnicon.minduslauncher.type;

import sonnicon.minduslauncher.core.Vars;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class Instance{
    public String name;
    public String version;
    public File file;
    public File jar;

    public Instance(){
        Vars.instances.add(this);
    }

    public Instance(File f){
        this();
        file = Vars.fileIO.createInstanceDir(f.getName());
        try{
            Files.copy(f.toPath(), new File(file, f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException ex){
            ex.printStackTrace();
            return;
        }
        jar = new File(file, f.getName());
        name = file.getName();
        version = Vars.fileIO.readGameVersion(f);
        f.deleteOnExit();
        Vars.instanceIO.saveInstanceJson(this);
        addToTable();
    }

    public Instance(HashMap<String, String> map, File file){
        this();
        name = map.get("name");
        version = map.get("version");
        this.file = file;
        jar = new File(file, map.get("jar"));
        addToTable();
    }

    public static Instance instanceFromURL(String url){
        String filename = url.substring(url.lastIndexOf("/"));
        File target = new File(Vars.tempDir, filename);
        try{
            ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fos = new FileOutputStream(target);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            return new Instance(target);
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void addToTable(){
        ((DefaultTableModel) Vars.launcherWindow.tableInstance.getModel()).addRow(new Object[]{name, version});
    }
}

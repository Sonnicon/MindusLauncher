package sonnicon.minduslauncher.core;

import com.google.gson.Gson;
import sonnicon.minduslauncher.files.Config;
import sonnicon.minduslauncher.files.FileIO;
import sonnicon.minduslauncher.files.InstanceIO;
import sonnicon.minduslauncher.type.Instance;
import sonnicon.minduslauncher.ui.windows.EditWindow;
import sonnicon.minduslauncher.ui.windows.LauncherWindow;
import sonnicon.minduslauncher.ui.windows.OfficialWindow;
import sonnicon.minduslauncher.ui.windows.SettingsWindow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Logger;

public class Vars{
    public static String[] args;
    public static ArgsHandler argsHandler;

    public static Gson gson = new Gson();
    public static Config config;

    public static LauncherWindow launcherWindow;
    public static EditWindow editWindow;
    public static OfficialWindow officialWindow;
    public static SettingsWindow settingsWindow;

    public static File rootDir;
    public static File instanceDir;
    public static File tempDir;

    public static FileIO fileIO;
    public static InstanceIO instanceIO;

    public static ArrayList<Instance> instances = new ArrayList<>();
    public static boolean loaded = false;

    public static boolean loadUI = true;

    public static void init(String[] arg){
        args = arg;

        try{
            rootDir = createDir(new File(Vars.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile(), "minduslauncher");
        }catch(URISyntaxException ex){
            Logger.getLogger(Vars.class.getName()).severe(ex.toString());
            System.exit(0);
        }
        instanceDir = createDir(rootDir, "instances");
        tempDir = createDir(rootDir, "temp");
        try{
            Files.walk(tempDir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }catch(IOException ex){
            Logger.getLogger(Vars.class.getName()).warning(ex.toString());
        }

        config = new Config();
        config.init();
        fileIO = new FileIO();
        instanceIO = new InstanceIO();

        argsHandler = new ArgsHandler();
        argsHandler.init();

        if(loadUI){
            launcherWindow = new LauncherWindow();
            editWindow = new EditWindow();
            officialWindow = new OfficialWindow();
            settingsWindow = new SettingsWindow();

            for(Instance i : Vars.instances){
                i.addToTable();
            }

            loaded = true;
            launcherWindow.show();
        }
        argsHandler.loaded();
    }

    public static File createDir(File file, String child){
        File result = new File(file, child);
        result.mkdirs();
        return result;
    }
}

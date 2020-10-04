package sonnicon.minduslauncher.core;

import com.google.gson.Gson;
import sonnicon.minduslauncher.files.Config;
import sonnicon.minduslauncher.files.FileIO;
import sonnicon.minduslauncher.files.InstanceIO;
import sonnicon.minduslauncher.type.Instance;
import sonnicon.minduslauncher.ui.EditWindow;
import sonnicon.minduslauncher.ui.LauncherWindow;
import sonnicon.minduslauncher.ui.OfficialWindow;
import sonnicon.minduslauncher.ui.SettingsWindow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

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

    public static void init(String[] arg){
        args = arg;

        try{
            rootDir = createDir(new File(Vars.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile(), "minduslauncher");
        }catch(URISyntaxException ex){
            ex.printStackTrace();
            System.exit(0);
        }
        instanceDir = createDir(rootDir, "instances");
        tempDir = createDir(rootDir, "temp");
        try{
            Files.walk(tempDir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::deleteOnExit);
        }catch(IOException ex){
            ex.printStackTrace();
        }

        config = Config.init();

        launcherWindow = new LauncherWindow();
        editWindow = new EditWindow();
        officialWindow = new OfficialWindow();
        settingsWindow = new SettingsWindow();

        fileIO = new FileIO();
        instanceIO = new InstanceIO();

        launcherWindow.show();

        argsHandler = new ArgsHandler();
    }

    public static File createDir(File file, String child){
        File result = new File(file, child);
        result.mkdirs();
        return result;
    }
}

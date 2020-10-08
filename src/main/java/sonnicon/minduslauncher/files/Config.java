package sonnicon.minduslauncher.files;

import com.google.gson.stream.JsonReader;
import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.config.CheckboxSetting;
import sonnicon.minduslauncher.type.config.ComboboxSetting;
import sonnicon.minduslauncher.type.config.Configuration;
import sonnicon.minduslauncher.type.config.Setting;
import sonnicon.minduslauncher.type.Window;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Config {
    protected File cfg = new File(Vars.rootDir, "config.json");
    protected HashMap<String, Configuration<?>> configurations = new HashMap<>();

    public void init(){

        HashMap<String, Object> read = new HashMap<>();
        if(cfg.exists()){
            try{
                 read = Vars.gson.fromJson(new JsonReader(new FileReader(cfg)), HashMap.class);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        if(read.containsKey("theme")) setTheme((String) read.get("theme"));

        new CheckboxSetting("openLog", (Boolean) read.getOrDefault("openLog", true), "Open Mindustry log");

        new Configuration<>("previous", (String) read.getOrDefault("previous", ""));
        new CheckboxSetting("selectPrevious", (Boolean) read.getOrDefault("selectPrevious", true), "Select previous instance on startup");

        new Configuration<>("latestTag", (String) read.getOrDefault("latestTag", ""));
        new CheckboxSetting("popupLatestTag", (Boolean) read.getOrDefault("popupLatestTag", true), "Popup new versions on startup");

        new ComboboxSetting("theme", (String) read.getOrDefault("theme", UIManager.getSystemLookAndFeelClassName()), "Theme"){
            @Override
            public String[] values(){
                UIManager.LookAndFeelInfo[] looksAndFeels = javax.swing.UIManager.getInstalledLookAndFeels();
                ArrayList<String> themeNames = new ArrayList<>();
                for (UIManager.LookAndFeelInfo lafi : looksAndFeels) {
                    themeNames.add(lafi.getName());
                }
                return themeNames.toArray(new String[]{});
            }

            @Override
            public void setValue(Object value){
                String name = "";
                for(UIManager.LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()){
                    if(lafi.getName().equals(value.toString())){
                        name = lafi.getClassName();
                    }
                }
                if(setTheme(name))
                    super.setValue(name);
            }

            @Override
            public void component(Container container){
                JComboBox<String> combobox = new JComboBox<>(values());
                container.add(combobox);
                combobox.getModel().setSelectedItem(UIManager.getLookAndFeel().getName());
                combobox.addActionListener(e -> setValue(combobox.getSelectedItem()));
            }
        };
    }

    public void addConfiguration(Configuration<?> configuration){
        configurations.put(configuration.getKey(), configuration);
    }

    public Setting<?>[] settings(){
        Object[] settings = configurations.values().stream().filter(c -> c instanceof Setting).toArray();
        return Arrays.copyOf(settings, settings.length, Setting[].class);
    }

    public Object get(String key){
        return configurations.get(key).getValue();
    }

    public void set(String key, Object value){
        configurations.get(key).setValue(value);
    }

    public void write(){
        HashMap<String, Object> map = new HashMap<>();
        configurations.forEach((s, configuration) -> map.put(s, configuration.getValue()));
        try {
            FileWriter writer = new FileWriter(cfg);
            Vars.gson.toJson(map, writer);
            writer.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    boolean setTheme(String className){
        try{
            UIManager.setLookAndFeel(className);
            Window.updateAll();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}

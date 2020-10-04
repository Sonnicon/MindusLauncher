package sonnicon.minduslauncher.files;

import com.google.gson.stream.JsonReader;
import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Window;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Config {
    protected static File cfg = new File(Vars.rootDir, "config.json");

    protected String theme = UIManager.getSystemLookAndFeelClassName();
    protected boolean openLog = true;

    protected String previous = "";
    protected boolean selectprevious = false;

    public static Config init(){
        if(cfg.exists()){
            try{
                Config config = Vars.gson.fromJson(new JsonReader(new FileReader(cfg)), Config.class);
                if(config == null) return new Config();

                if(config.theme.length() > 0){
                    config.setTheme(config.theme);
                }
                return config;
            }catch(Exception ex){
                ex.printStackTrace();
                return new Config();
            }
        }
        return new Config();
    }

    public void setTheme(String theme){
        try {
            UIManager.setLookAndFeel(theme);
            Window.updateAll();
        }catch (Exception ex){
            ex.printStackTrace();
            return;
        }
        this.theme = theme;
    }

    public String getTheme(){
        return theme;
    }

    public void setOpenLog(boolean open){
        this.openLog = open;
    }

    public boolean getOpenLog(){
        return openLog;
    }

    public void setSelectPrevious(boolean selectprevious){
        this.selectprevious = selectprevious;
    }

    public boolean getSelectPrevious(){
        return selectprevious;
    }

    public void setPrevious(String previous){
        this.previous = previous;
    }

    public String getPrevious(){
        return previous;
    }

    public static void write(){
        try {
            FileWriter writer = new FileWriter(cfg);
            Vars.gson.toJson(Vars.config, writer);
            writer.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}

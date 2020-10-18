package sonnicon.minduslauncher.files;

import com.google.gson.stream.JsonReader;
import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Instance;

import java.io.*;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class InstanceIO{

    public InstanceIO(){
        File[] files = Vars.instanceDir.listFiles();
        if(files == null) return;
        for(File f : files){
            File data = new File(f, "instance.json");
            if(!data.exists() || !data.canRead()) continue;
            try{
                JsonReader reader = new JsonReader(new FileReader(data));
                Instance i = Vars.gson.fromJson(reader, Instance.class);
                i.init(f);
                reader.close();
            }catch(Exception ex){
                Logger.getLogger(getClass().getName()).warning(ex.toString());
            }
        }
    }

    public void saveInstanceJson(Instance instance){
        try{
            FileWriter writer = new FileWriter(new File(instance.file, "instance.json"));
            Vars.gson.toJson(instance, writer);
            writer.close();
        }catch(IOException ex){
            Logger.getLogger(getClass().getName()).warning(ex.toString());
        }
    }
}

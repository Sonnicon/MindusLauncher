package sonnicon.minduslauncher.files;

import com.google.gson.stream.JsonReader;
import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Instance;

import java.io.*;
import java.util.HashMap;
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
                new Instance(Vars.gson.fromJson(reader, HashMap.class), f);
                reader.close();
            }catch(Exception ex){
                Logger.getLogger(getClass().getName()).warning(ex.toString());
            }
        }
    }

    public void saveInstanceJson(Instance instance){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", instance.name);
        map.put("version", instance.version);
        map.put("cmdargs", instance.cmdArgs);
        map.put("mindustryargs", instance.mindustryArgs);
        map.put("jar", instance.jar.getName());
        try{
            FileWriter writer = new FileWriter(new File(instance.file, "instance.json"));
            Vars.gson.toJson(map, writer);
            writer.close();
        }catch(IOException ex){
            Logger.getLogger(getClass().getName()).warning(ex.toString());
        }
    }
}

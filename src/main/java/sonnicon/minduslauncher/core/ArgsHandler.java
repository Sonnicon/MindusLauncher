package sonnicon.minduslauncher.core;

import sonnicon.minduslauncher.type.Command;
import sonnicon.minduslauncher.type.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

public class ArgsHandler{
    private final ArrayList<Runnable> onLoaded = new ArrayList<>();
    private final HashMap<String, Command> commands = new HashMap<>();

    protected void init(){
        Command.init(Vars.args);

        // create commands

        Command launch = new Command("launch"){
            @Override
            public Object call(Object child){
                if(child != null)
                    ((Instance) child).launch();
                return null;
            }
        };

        new Command("instance", launch){
            @Override
            public Object call(Object child){
                for(Instance instance : Vars.instances){
                    if(instance.file.getName().equals(child)){
                        return instance;
                    }
                }
                Logger.getLogger(getClass().getName()).warning("Unable to find instance '" + child + "' for 'launch' arg");
                return null;
            }
        }.setHasChildValue(true);

        new Command("download", launch){
            @Override
            public Object call(Object child){
                return Instance.instanceFromURL((String) child);
            }
        }.setHasChildValue(true);


        new Command("noui"){
            @Override
            public Object call(Object child){
                Vars.loadUI = false;
                return null;
            }
        };

        //todo help command


        // run commands

        while(Command.index < Command.args.length){
            if(!Command.args[Command.index].startsWith("-")){
                Logger.getLogger(getClass().getName()).severe("Major arguments start with '-'!");
                System.exit(64);
            }
            String key = Command.args[Command.index].substring(1);
            if(commands.containsKey(key)){
                commands.get(key).execute();
                Command.index++;
            }else{
                Logger.getLogger(getClass().getName()).severe("Unknown major argument '" + key + "'!");
                System.exit(64);
            }
        }
    }

    public void loaded(){
        onLoaded.forEach(Runnable::run);
    }

    public void addCommand(String key, Command command){
        commands.put(key, command);
    }
}

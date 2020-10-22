package sonnicon.minduslauncher.core;

import sonnicon.minduslauncher.files.FileIO;
import sonnicon.minduslauncher.type.Command;
import sonnicon.minduslauncher.type.HelpCommand;
import sonnicon.minduslauncher.type.Instance;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class ArgsHandler{
    private final ArrayList<Runnable> onLoaded = new ArrayList<>();
    private final HashMap<String, Command> commands = new HashMap<>();

    protected void init(){
        Command.init(Vars.args);

        // create commands

        new HelpCommand().setDesc("Displays a tree of commands with descriptions");

        Command instances = new Command().addToRoot("instances");

        Command launch = new Command(child -> {
            if(child != null)
                ((Instance) child).launch();
            return null;
        }, "launch").setDesc("Launch an instance");

        Command create = new Command(child -> new Instance((File) child), "create", instances).setDesc("Create an instance from a file");

        Command delete = new Command(child -> {
            ((Instance) child).delete();
            return null;
        }, "delete", instances).setDesc("Delete an instance");

        new Command(child -> {
            for(Instance instance : Vars.instances){
                if(instance.file.getName().equals(child)){
                    return instance;
                }
            }
            Logger.getLogger(getClass().getName()).warning("Unable to find instance '" + child);
            return null;
        }, "name", launch, delete   ).setHasChildValue(true).setDesc("Get an existing instance by name");

        new Command(child -> FileIO.fileFromURL((String) child), "download", create).setHasChildValue(true).setDesc("Download a file from URL");

        new Command(child -> new File((String) child), "file", create).setHasChildValue(true).setDesc("Use a file from filesystem");

        new Command(child -> {
                Vars.loadUI = false;
                return null;
        }, "noui").setDesc("Don't initialize or open any UI");

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

    public HashMap<String, Command> getCommands(){
        return commands;
    }
}

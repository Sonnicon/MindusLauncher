package sonnicon.minduslauncher.type;

import sonnicon.minduslauncher.core.Vars;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class Command{
    public static int index = 0;
    public static String[] args;

    protected HashMap<String, Command> childCommands;
    protected boolean hasChildValue = false;

    public String desc = "No description.";
    public Function<Object, Object> function;

    public Command(){}

    public Command(Function<Object, Object> function, String key, Command... parents){
        setFunction(function);
        if(parents.length == 0){
            addToRoot(key);
        }else{
            for(Command c : parents){
                c.addSubCommand(key, this);
            }
        }
    }

    public final Object execute(){
        if(!hasChildValue && childCommands == null){
            return call(null);
        }
        if(args.length <= ++index){
            Logger.getLogger(getClass().getName()).severe("Incomplete Argument!");
            System.exit(64);
            return null;
        }

        String arg = args[index];
        if(childCommands != null && arg.startsWith("-")){
            String child = arg.substring(1);
            if(childCommands.containsKey(child)){
                return call(childCommands.get(child).execute());
            }else{
                Logger.getLogger(getClass().getName()).severe("Unknown sub-argument '" + arg + "'!");
                System.exit(64);
                return null;
            }
        }else if(hasChildValue){
            return call(arg);
        }else{
            Logger.getLogger(getClass().getName()).severe("Unexpected value '" + arg + "'!");
            System.exit(64);
            return null;
        }
    }

    protected Object call(Object child){
        return function.apply(child);
    }

    public void addSubCommand(String key, Command c){
        if(childCommands == null) childCommands = new HashMap<>();
        childCommands.put(key, c);
    }

    public Command addToRoot(String key){
        Vars.argsHandler.addCommand(key, this);
        return this;
    }

    public Command setHasChildValue(boolean childValue){
        this.hasChildValue = childValue;
        return this;
    }

    public Command setDesc(String desc){
        // epic optimization
        this.desc = desc + ".";
        return this;
    }
    public Command setFunction(Function<Object, Object> function){
        this.function = function;
        return this;
    }

    public static void init(String[] args){
        Command.args = args;
        index = 0;
    }
}

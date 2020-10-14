package sonnicon.minduslauncher.type;

import sonnicon.minduslauncher.core.Vars;

import java.util.ArrayList;

public class HelpCommand extends Command{

    public HelpCommand(String key){
        super(key);
    }

    @Override
    public Object call(Object child){
        ArrayList<String> output = new ArrayList<>();
        output.add("Help (command order matters):");
        Vars.argsHandler.getCommands().forEach((s, c) -> {
            output.add("'-" + s + "' = " + c.desc);
            addCommands(c, output, 1);
        });
        output.forEach(System.out::println);
        System.exit(0);
        return null;
    }

    protected void addCommands(Command command, ArrayList<String> output, int layer){
        if(command.childCommands != null){
            command.childCommands.forEach((s, c) -> {
                output.add(new String(new char[layer]).replace("\0", "    ") + "'-" + s + "' = " + c.desc);
                addCommands(c, output, layer + 1);
            });
        }
        if(command.hasChildValue){
            output.add(new String(new char[layer]).replace("\0", "    ") + "<value>");
        }
    }
}

package sonnicon.minduslauncher.core;

import sonnicon.minduslauncher.type.Instance;

public class ArgsHandler{

    public ArgsHandler(){
        for(int i = 0; i < Vars.args.length; i++){
            switch(Vars.args[i]){
                case("-launch"):{
                    if(Vars.args.length >= ++i){
                        boolean found = false;
                        for(Instance instance : Vars.instances){
                            if(instance.file.getName().equals(Vars.args[i])){
                                instance.launch();
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            System.out.println("Unable to find instance '" + Vars.args[i] + "' for 'launch' arg");
                        }
                    }else{
                        System.out.println("Missing instance name for 'launch' arg.");
                    }
                    break;
                }
                default:{
                    System.out.println("Unknown arg '" + Vars.args[i] + "'");
                    System.exit(64);
                }
            }
        }
    }
}

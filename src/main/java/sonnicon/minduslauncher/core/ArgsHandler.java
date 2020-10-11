package sonnicon.minduslauncher.core;

import sonnicon.minduslauncher.type.Instance;

import java.util.ArrayList;

public class ArgsHandler{
    private ArrayList<Runnable> onLoaded = new ArrayList<>();

    public ArgsHandler(){
        for(int i = 0; i < Vars.args.length; i++){
            switch(Vars.args[i]){
                case("-launch"):{
                    if(Vars.args.length >= ++i){
                        if(Vars.args[i].equals("-downloadurl")){
                            if(Vars.args.length >= i + 1){
                                int finalI = ++i;
                                onLoaded.add(() -> Instance.instanceFromURL(Vars.args[finalI]).launch());
                            }else{
                                System.out.println("Please provide URL for for '-downloadurl' arg");
                            }
                        }else{
                            int finalI = i;
                            onLoaded.add(() -> {
                                boolean found = false;
                                for(Instance instance : Vars.instances){
                                    if(instance.file.getName().equals(Vars.args[finalI])){
                                        instance.launch();
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found){
                                    System.out.println("Unable to find instance '" + Vars.args[finalI] + "' for 'launch' arg");
                                }

                            });
                        }
                    }else{
                        System.out.println("Missing instance name for 'launch' arg.");
                    }
                    break;
                }
                case("-noui"):{
                    Vars.loadUI = false;
                    break;
                }

                default:{
                    System.out.println("Unknown arg '" + Vars.args[i] + "'");
                    System.exit(64);
                }
            }
        }
    }

    public void loaded(){
        onLoaded.forEach(Runnable::run);
    }
}

package sonnicon.minduslauncher.type.config;

import sonnicon.minduslauncher.core.Vars;

public class Configuration<T>{
    protected T value;
    protected String key;

    public Configuration(String key, T value){
        this.key = key;
        this.value = value;

        Vars.config.addConfiguration(this);
    }

    public String getKey(){
        return this.key;
    }

    public T getValue(){
        return this.value;
    }

    public void setValue(Object value){
        this.value = (T) value;
    }
}

package sonnicon.minduslauncher.core;

import java.io.File;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

//todo loader mods
public class MindustryLauncher{
    public static void main(String[] args){
        if(args.length != 1) return;
        try{
            File f = new File(args[0]);
            JarFile jar = new JarFile(f);
            String mainClass = jar.getManifest().getMainAttributes().getValue("Main-Class");
            jar.close();
            Class c = Class.forName(mainClass, true, new URLClassLoader(new java.net.URL[]{f.toURI().toURL()}));
            c.getMethod("main", String[].class).invoke(null, new Object[]{new String[]{"test"}});
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}

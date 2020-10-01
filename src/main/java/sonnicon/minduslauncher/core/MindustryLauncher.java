package sonnicon.minduslauncher.core;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.JarFile;

public class MindustryLauncher{
    public static void main(String[] args){
        if(args.length < 1) return;
        try{
            File f = new File(args[0]);
            JarFile jar = new JarFile(f);
            String mainClass = jar.getManifest().getMainAttributes().getValue("Main-Class");
            jar.close();

            ArrayList<URL> urls = new ArrayList<>();
            urls.add(f.toURI().toURL());


            File moddir = new File(f.getParent(), "loadermods");
            if(!moddir.exists()) moddir.mkdirs();
            for(File mod : moddir.listFiles()){
                urls.add(mod.toURI().toURL());
            }
            URLClassLoader classloader = new URLClassLoader(urls.toArray(new URL[]{}));

            for (File mod : moddir.listFiles()){
                JarFile modjar = new JarFile(mod);
                classloader.loadClass(modjar.getManifest().getMainAttributes().getValue("Main-Class"))
                        .getMethod("main", String[].class).invoke(null, new Object[]{new String[]{}});
                modjar.close();
            }

            Class c = Class.forName(mainClass, true, classloader);
            c.getMethod("main", String[].class).invoke(null, new Object[]{Arrays.copyOfRange(args, 1, args.length)});
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}

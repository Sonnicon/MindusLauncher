package sonnicon.minduslauncher.files;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Instance;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileIO{
    private static final Pattern filenamePattern = Pattern.compile("\\([0-9]+\\)\\Z");

    public File createInstanceDir(String jarname){
        if(jarname == null || jarname.length() == 0 || !jarname.endsWith(".jar")) return null;
        //create dirs for instance with collision protection
        String name = jarname.substring(0, jarname.length() - 4);
        File target = new File(Vars.instanceDir, name);
        while(target.exists()){
            if(filenamePattern.matcher(name).find()){
                int nextID = Integer.parseInt(name.substring(name.lastIndexOf("(") + 1, name.length() - 1));
                name = name.substring(0, name.lastIndexOf("(") + 1);
                do{
                    target = new File(Vars.instanceDir, name + ++nextID + ")");
                }while(target.exists());
            }else{
                name += "(1)";
                target = new File(Vars.instanceDir, name);
            }
        }
        target.mkdirs();
        new File(target, "loadermods").mkdir();

        return target;
    }

    public String readGameVersion(File jar){
        try{
            ZipFile zip = new ZipFile(jar);
            ZipEntry entry = zip.getEntry("version.properties");
            if(entry == null) return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));

            String[] result = new String[2];
            while(reader.ready()){
                String line = reader.readLine();
                if(line.startsWith("#")) continue;
                String[] split = line.split("=");
                if(split[0].equals("number")) result[0] = split[1];
                else if(split[0].equals("build")) result[1] = split[1];
            }
            reader.close();
            return result[0] + " b" + result[1];
        }catch(IOException ex){
            Logger.getLogger(getClass().getName()).warning(ex.toString());
            return null;
        }
    }

    public static File fileFromURL(String url){
        String filename = url.substring(url.lastIndexOf("/"));
        File target = new File(Vars.tempDir, filename);
        try{
            ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fos = new FileOutputStream(target);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            return target;
        }catch(Exception ex){
            Logger.getLogger(Instance.class.getName()).warning(ex.toString());
            return null;
        }
    }

    public static void compileMindustry(File zip){
        if((JOptionPane.showConfirmDialog(Vars.launcherWindow.getFrame(), "This may take a while. Are you sure you want to continue?", "Are you sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION))
            return;
        try{
            File out = new File(Vars.tempDir, zip.getName().substring(0, zip.getName().indexOf(".")));
            out.mkdirs();
            new net.lingala.zip4j.ZipFile(zip).extractAll(out.getPath());
            File dir = new File(out, out.list()[0]);
            Process p = Runtime.getRuntime().exec(new File(dir, (System.getProperty("os.name").toLowerCase().contains("win") ? "/gradlew.bat" : "./gradlew")).getPath() + " desktop:dist", null, dir);
            while(p.isAlive()){
                Thread.sleep(1000);
            }
            new Instance(new File(dir, "desktop/build/libs/").listFiles()[0]);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
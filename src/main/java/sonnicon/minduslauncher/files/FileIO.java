package sonnicon.minduslauncher.files;

import sonnicon.minduslauncher.core.Vars;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
            ex.printStackTrace();
            return null;
        }
    }
}

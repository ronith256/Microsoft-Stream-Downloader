import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Base64;
import java.util.Scanner;

public class Config {
    static String currentPath = System.getProperty("user.dir");
    static File configFile;
    public void config(){
        currentPath = currentPath + "\\config";

        File configFolder = new File(currentPath);
        if(!configFolder.exists()){
            configFolder.mkdir();
        }

        if(!new File(currentPath + "\\ffmpeg.exe").exists()){
            if(!new File(currentPath + "\\ffprobe.exe").exists()){
                JOptionPane.showMessageDialog(GUI.frame,
                        "ffmpeg/ffprobe does not exist\n"+
                                "Please download from \"https://www.gyan.dev/ffmpeg/builds/\" and extract it to the config folder",
                        "FFmpeg not found!",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        currentPath = currentPath + "\\config.txt";
        configFile = new File(currentPath);
        if(configFile.exists()){
            try{
            Scanner ab = new Scanner(configFile);
            String line = ab.next();
            line = line.substring(line.indexOf(":")+1);
            GUI.emailField.setText(line);
            line = ab.next();
            line = line.substring(line.indexOf(":")+1);
            byte[] e = Base64.getDecoder().decode(line);
            line = new String(e);
            GUI.passwordField.setText(line);
            }catch (Exception ignored){};
        }
    }

    public void writeConfig(){
        if(!configFile.exists()){
            try{
            Boolean a = configFile.createNewFile();
                System.out.println(a);
            }catch(Exception ignored){};
        }
        if(configFile.exists()){
                try{
                FileWriter writer = new FileWriter(configFile);
                writer.write("email:" + GUI.emailField.getText() + "\n" +
                        "password:" + Base64.getEncoder().encodeToString(String.valueOf(GUI.passwordField.getPassword()).getBytes()));
                writer.close();
                }catch(Exception ignored){};
        }
    }
}

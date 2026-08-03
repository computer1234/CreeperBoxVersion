package helper.creeperbox.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogUtil {

    public static void Log(String msg){
        Log.d("com.creeperbox",msg);
    }

    public static void Log(Object msg){
        Log.d("com.creeperbox",msg==null?"null":msg.toString());
    }

    public static void write(String data){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/data/local/tmp/1.txt"))){
            writer.write(data);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}

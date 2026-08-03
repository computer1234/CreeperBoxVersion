package helper.creeperbox.hotfix;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import dalvik.system.DexClassLoader;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

public class UpdateManager {
    private MyDexClassLoader classLoader;


    public Module testModule;

    private static final String DEXPATH = "/data/local/tmp/a.dex";

    private static final String NOTIPATH = "/data/local/tmp/a.txt";

    private boolean first = true;


    public UpdateManager(){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkAndUpdate();
            }
        };
        timer.schedule(task, 3000, 3000);
    }


    public boolean checkNeedUpdate(){

        File updateFile = new File(NOTIPATH);
        if(!updateFile.exists()){
            return false;
        }
        if (updateFile.length() == 0) {
            return false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(updateFile);
            fos.write(new byte[0]);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public void checkAndUpdate(){
        if(CreeperBox.INSTANCE.context == null){
            return;
        }


        if(!new File(DEXPATH).exists()){
            return;
        }


        if(!checkNeedUpdate() && !first){
            return;
        }

        first = false;
        classLoader = null;
        if(testModule!=null){
            CreeperBox.INSTANCE.getModuleManager().unregisterModule(testModule);
        }

        testModule = null;
        System.gc();

        try {
            classLoader = new MyDexClassLoader(DEXPATH, CreeperBox.INSTANCE.context,getClass().getClassLoader());
            Object obj = classLoader.loadClass("com.hopeclient.feature.module.modules.ChestStealer").newInstance();
            if(!(obj instanceof Module)){
                throw new RuntimeException("not a module");
            }
            testModule = (Module) obj;
            CreeperBox.INSTANCE.getModuleManager().registerModule(testModule);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player!=null) {
            player.displayClientMessage("测试模块更新成功!");
        }
    }
}



class MyDexClassLoader extends DexClassLoader {
    public MyDexClassLoader(String dexPath, Context context, ClassLoader parent) {
        super(dexPath, context.getDir("dex",0).getAbsolutePath(), null, parent);
    }
}
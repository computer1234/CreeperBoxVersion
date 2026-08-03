package helper.creeperbox.feature.module.modules.build;

import android.annotation.SuppressLint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;

@ModuleInfo(name = "清除游客数据", category = Category.Build)
public class ClearAccount extends Module {


    @Override
    public void onEnable() {
        clearVisitorData();
    }

    public static void clearVisitorData() {
        File dataFile = new File(CreeperBox.INSTANCE.activity.getDataDir(),"shared_prefs");
        if(!dataFile.exists()) return;
        File[] list = dataFile.listFiles();
        if(list == null) return;
        for(File f : list){
            if(f.isFile()) f.delete();
        }
    }




}

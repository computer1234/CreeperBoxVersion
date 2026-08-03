package helper.creeperbox.hook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import helper.creeperbox.clickgui.ClickGUI;
import helper.creeperbox.clickgui.ClickIcon;
import helper.creeperbox.clickgui.VerifyGUI;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.sdk.InstanceGenerator;

import java.util.List;

public class HopeHookEntrance {

    private static String getSoPath(Activity activity,String packageName, String moduleName) {
        PackageManager pm = activity.getApplicationContext().getPackageManager();
        List<PackageInfo> pkgList = pm.getInstalledPackages(0);
        if(pkgList.size() > 0) {
            for (PackageInfo pi : pkgList) {
                if (pi.applicationInfo.publicSourceDir.indexOf(packageName) != -1) {
                    return pi.applicationInfo.publicSourceDir.replace("base.apk", "lib/arm64/lib" + moduleName + ".so");
                }
            }
        }
        return null;
    }

    public static boolean hasInit = false;


    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void entry(Activity activity) {
        CreeperBox.INSTANCE.activity = activity;

//        String path = getSoPath(activity, "com.netease.x19", "creeperbox");
//        Log.d("tatsau",path+"");
//        if(path != null){
//            System.load(path);
//        }else {
//            System.loadLibrary("creeperbox");
//        }
        try {
            PackageManager pm = activity.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo("helper.creeperbox", 0);
            String soPath = appInfo.nativeLibraryDir + "/libcreeperbox.so";
            System.load(soPath);
        } catch (Exception e) {
        }



        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                InstanceGenerator.init();
                CreeperBox.INSTANCE.init();
                hasInit = true;
                new VerifyGUI(activity);
                if(!CreeperBox.INSTANCE.debug){
                    new ClickGUI(activity);
                    new ClickIcon(activity);
                }
            }
        }, 5000);
    }

}

package helper.creeperbox.hook;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import helper.creeperbox.clients.CreeperBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookInit implements IXposedHookLoadPackage {

    private static final String TAG = "CreeperBox";
    private static final String CRASHHUNTER_CONFIG_PATH = "/data/user/0/com.netease.x19/files/crashhunter_config/crashhunter_config.txt";
    private static final String NEW_CONFIG_CONTENT = "{\"id\":1670,\"project\":\"g79\",\"os_type\":\"android\",\"enable\":false,\"turn_on\":false,\"wifi_only\":false,\"expire\":30,\"queue_size\":10,\"file_limit\":6144,\"carrier_limit\":6144,\"di_refresh_interval\":300,\"breakpad_enabled\":true,\"java_crash_enabled\":true,\"watchdog_enabled\":true,\"message_enabled\":true,\"app_crash_throwable\":true,\"uncaught_exception_enabled\":true,\"uncaught_exception_emulator_option\":[],\"screenshot\":[],\"acsdk_enabled\":false,\"logger_enabled\":true,\"local_unwind\":true,\"launch_crash_enabled\":true,\"game_not_response_enabled\":false,\"lag_active_detection\":false,\"lag_time\":5,\"lag_history\":false,\"lag_detection_count\":5,\"thread_snapshot_limit\":3,\"thread_snapshot_frame_max\":30}";

    public static ClassLoader moduleClassLoader;
    private static boolean loaded = false;
    private static boolean configModified = false;

    private boolean needHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        return loadPackageParam.appInfo != null &&
                new File(loadPackageParam.appInfo.nativeLibraryDir, "libminecraftpe.so").exists();
    }

    private void modifyCrashHunterConfig() {
        if (configModified) return;
        configModified = true;

        File configFile = new File(CRASHHUNTER_CONFIG_PATH);

        // 读取并打印原内容
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                Log.d(TAG, "[CrashHunter] Original content: " + content.toString());
            } catch (IOException e) {
                Log.d(TAG, "[CrashHunter] Failed to read config: " + e.getMessage());
            }
        } else {
            Log.d(TAG, "[CrashHunter] Config file does not exist, creating...");
            // 确保目录存在
            File parentDir = configFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
        }

        // 写入新内容
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(NEW_CONFIG_CONTENT);
            Log.d(TAG, "[CrashHunter] Config written successfully");
        } catch (IOException e) {
            Log.d(TAG, "[CrashHunter] Failed to write config: " + e.getMessage());
        }
    }


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (needHook(lpparam)) {
            // 修改 CrashHunter 配置
            modifyCrashHunterConfig();

            XposedHelpers.findAndHookMethod(
                    Activity.class,
                    "onResume",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Activity activity = (Activity) param.thisObject;
                            CreeperBox.INSTANCE.context = activity;
                            if(activity.getClass().getName().equals("com.mojang.minecraftpe.MainActivity")){
                                 if (!loaded) {
                                     HopeHookEntrance.entry(activity);
                                     loaded = true;
                                 }
                            }
                        }
                    }
            );
        }

    }



}

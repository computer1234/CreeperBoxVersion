package helper.creeperbox.feature.module.modules.render;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.LoginEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.utils.FileUtil;

@ModuleInfo(name = "加载模型皮肤", category = Category.Render)
public class LoadSkin extends Module {

    public long ONE_HOUR = 60 * 60 * 1000;
    public long ONE_DAY = 24 * ONE_HOUR;
    public long SEVEN_DAYS_ONE_HOUR = 7 * ONE_DAY + ONE_HOUR;

    @Override
    public void onEnable() {

        File destFile1 = new File(CreeperBox.INSTANCE.context.getDataDir(),"files/games/com.netease/skin_packs/custom.zip");
        File destFile2 = new File(CreeperBox.INSTANCE.context.getDataDir(),"files/games/com.netease/skin_packs/custom.png");

        File srcFile1 = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),"skin.zip");
        File srcFile2 = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),"skin.png");

        if(srcFile1.exists()){
            try {
                FileUtil.copyFile(srcFile1,destFile1);
                CreeperBox.a1(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if(srcFile2.exists()){
            try {
                FileUtil.copyFile(srcFile2,destFile2);
                CreeperBox.a1(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

    }


    @Override
    public void onDisable() {
        CreeperBox.a1(0);
    }

    @SubscribeEvent
    public void onLogin(LoginEvent event){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(event.loginData, JsonObject.class);
        jsonObject.addProperty("PersonaSkin", true);
        event.loginData = gson.toJson(jsonObject);
    }

}

package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;

@ModuleInfo(name = "绕过验证发言", category = Category.Survival)
public class ChatBypass extends Module {

    public static boolean bypass;


    @Override
    public void onEnable() {
        bypass = true;
    }


    @Override
    public void onDisable() {
        bypass = false;
    }
}

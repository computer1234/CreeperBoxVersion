package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;


@ModuleInfo(name = "开关提示", category = Category.Render)
public class Notification extends Module {

    public static boolean toggle;

    @Override
    public void onEnable() {
        toggle = true;
    }


    @Override
    public void onDisable() {
        toggle = false;
    }
}

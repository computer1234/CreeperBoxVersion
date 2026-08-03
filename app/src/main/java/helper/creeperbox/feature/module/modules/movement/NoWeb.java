package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "无视蜘蛛网", category = Category.Movement)
public class NoWeb extends Module {
    @Override
    public void onEnable() {
        EntityLocalPlayer.setNoWeb(true);
    }

    @Override
    public void onDisable() {
        EntityLocalPlayer.setNoWeb(false);
    }
}

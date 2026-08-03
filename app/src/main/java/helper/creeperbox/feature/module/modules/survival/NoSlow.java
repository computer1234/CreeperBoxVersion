package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "消除减速", category = Category.Survival)
public class NoSlow extends Module {
    @Override
    public void onEnable() {
        EntityLocalPlayer.setNoSlow(true);
    }

    @Override
    public void onDisable() {
        EntityLocalPlayer.setNoSlow(false);
    }
}

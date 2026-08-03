package helper.creeperbox.feature.module.modules.build;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "强开坐标", category = Category.Build)
public class ForcePos extends Module {

    @Override
    public void onEnable() {
        EntityLocalPlayer.setForcePos(true);
    }

    @Override
    public void onDisable() {
        EntityLocalPlayer.setForcePos(false);
    }
}

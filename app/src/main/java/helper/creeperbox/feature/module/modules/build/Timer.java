package helper.creeperbox.feature.module.modules.build;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "全局变速", category = Category.Build)
public class Timer extends Module {
    private final NumberValue timer = new NumberValue("速度", this,40,1, 100, 1);

    @Override
    public void onEnable() {
        EntityLocalPlayer.setTimer(timer.getCurrentValue().floatValue());
    }
    @Override
    public void onDisable() {
        EntityLocalPlayer.setTimer(20f);
    }


    @Override
    public String getTag() {
        return String.valueOf(timer.getCurrentValue().intValue());
    }

}

package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.feature.component.BlinkComponent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;

@ModuleInfo(name = "延迟发包", category = Category.Survival)
public class Blink extends Module {

    @Override
    public void onEnable() {
        BlinkComponent.blink = true;
        BlinkComponent.setExemptedPackets();
    }

    @Override
    public void onDisable() {
        BlinkComponent.blink = false;
        BlinkComponent.dispatch();
    }


}

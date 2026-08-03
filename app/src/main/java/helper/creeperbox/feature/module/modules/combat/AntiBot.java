package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;

@ModuleInfo(name = "EC反假人", category = Category.Combat)
public class AntiBot extends Module {
    private static AntiBot INSTANCE;
    public AntiBot(){
        INSTANCE = this;
    }

    public static AntiBot getINSTANCE() {
        return INSTANCE;
    }
}

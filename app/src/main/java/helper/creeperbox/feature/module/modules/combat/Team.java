package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;


@ModuleInfo(name = "智能队友", category = Category.Combat)
public class  Team extends Module {

    private static Team INSTANCE;

    public Team(){
        INSTANCE = this;
    }

    public static Team getINSTANCE() {
        return INSTANCE;
    }



}
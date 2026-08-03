package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;


@ModuleInfo(name = "判定类型", category = Category.Combat)
public class Target extends Module {
    public final ListValue target = new ListValue("对象",this,"玩家")
            .addSubList("玩家")
            .addSubList("实体");


    private static Target INSTANCE;

    public Target(){
        INSTANCE = this;
    }

    public static Target getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public String getTag() {
        switch(target.getCurrentValue()) {
            case "玩家":
                return "Player";
            default:
                return "Creature";
        }
    }

}

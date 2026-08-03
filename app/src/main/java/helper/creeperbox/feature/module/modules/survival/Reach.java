package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "长臂猿", category = Category.Survival)
public class Reach extends Module {

    public final NumberValue attackRange = new NumberValue("攻击", this,5, 3, 10, 0.1);
    public final NumberValue buildRange = new NumberValue("放置", this,5, 3, 10, 0.1);


    public Reach(){
        attackRange.setOnValueChange((number)->{
            if(isEnable()){
                EntityLocalPlayer.setAttackRange(number.floatValue());
            }
            return number;
        } );

        buildRange.setOnValueChange((number)->{
            if(isEnable()){
                EntityLocalPlayer.setBuildRange(number.floatValue());
            }
            return number;
        } );
    }

    @Override
    public void onEnable() {
        EntityLocalPlayer.setAttackRange(attackRange.getCurrentValue().floatValue());
        EntityLocalPlayer.setBuildRange(buildRange.getCurrentValue().floatValue());
    }

    @Override
    public void onDisable() {
        EntityLocalPlayer.setAttackRange(3f);
        EntityLocalPlayer.setBuildRange(0f);
    }


}

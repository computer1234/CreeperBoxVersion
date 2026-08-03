package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.Abilities;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "飞行权限", category = Category.Movement)
public class FlyAbility extends Module {



    @SubscribeEvent
    public void onTick(TickEvent event) {
        event.getPlayer().setAbilityBool(Abilities.MayFly,true);
    }

    @Override
    public void onDisable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player!=null){
            player.setAbilityBool(Abilities.MayFly,false);
            player.setAbilityBool(Abilities.Flying,false);
        }
    }
}

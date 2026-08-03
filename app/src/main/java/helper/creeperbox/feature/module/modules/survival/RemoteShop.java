package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "远程商店", category = Category.Survival)
public class RemoteShop extends Module {

    @Override
    public void onEnable() {
        EntityLocalPlayer player =
        CreeperBox.INSTANCE.getLocalPlayer();

        if(player!=null){
            for(EntityActor actor : player.getLevel().getRuntimeActorList()){
                if(actor.getNameSpace().contains("minecraft:villager")){
                    player.attack(actor);
                    return;
                }
            }
        }
    }





}

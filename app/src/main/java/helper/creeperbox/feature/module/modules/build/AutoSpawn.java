package helper.creeperbox.feature.module.modules.build;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.Attributes;
import helper.creeperbox.sdk.math.Vec3f;

@ModuleInfo(name = "原地复活", category = Category.Build)
public class AutoSpawn extends Module {

    @Override
    public void onEnable() {
        lastDeath = false;
    }
    private Vec3f lastPos;

    private boolean lastDeath = false;
    @SubscribeEvent
    public void onTick(TickEvent event) {
        if(lastPos == null) {
            if (event.getPlayer().getAttributeCurrentValue(Attributes.HEALTH) > 0){
                lastPos = event.getPlayer().getPos();
            }
        }else {
            if (event.getPlayer().getAttributeCurrentValue(Attributes.HEALTH) > 0){
                if(lastDeath){
                    event.getPlayer().setPos(lastPos);
                    lastDeath = false;
                    lastPos = null;
                }else{
                    lastPos = event.getPlayer().getPos();
                }
                return;
            }
            lastDeath = true;;
        }

    }



}

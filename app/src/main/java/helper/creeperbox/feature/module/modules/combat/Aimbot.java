package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render3DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.utils.mc.RotationUtil;
import helper.creeperbox.utils.mc.TargetUtil;

@ModuleInfo(name = "自动瞄准", category = Category.Combat)
public class Aimbot extends Module {

    private final NumberValue range = new NumberValue("范围", this, 3, 1, 20, 0.1);

    @SubscribeEvent
    public void onAim(Render3DEvent event){

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player!=null){
            EntityActor actor = TargetUtil.findNearest(player,range.getCurrentValue().floatValue());
            if(actor!=null){
                Vec2f rot = RotationUtil.calcRotation(player,actor.getPos());
                Vec2f current = player.getRotation();
                player.applyTurnDelta(new Vec2f(current.x-rot.x,rot.y-current.y));
            }
        }

    }

}

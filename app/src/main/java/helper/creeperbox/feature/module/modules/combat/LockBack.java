package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.mc.TargetUtil;

@ModuleInfo(name = "锁背", category = Category.Combat)
public class LockBack extends Module {

    private final NumberValue range = new NumberValue("范围", this,3,1, 100, 1);

    private final NumberValue distance = new NumberValue("距离", this,10,1, 1000, 1);


    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityActor target = TargetUtil.findNearest(event.getPlayer(),distance.getCurrentValue().floatValue());
        if(target!=null){
            Vec3f pos = target.getPos();
            float direction = (float) Math.toRadians(target.getRotation().y+180);
            event.getPlayer().setMotion(new Vec3f(0,0,0));
            float range = this.range.getCurrentValue().floatValue();
            event.getPlayer().setPos(new Vec3f(
                    pos.x - (float) Math.sin(direction) * range,
                    pos.y,
                    pos.z + (float) Math.cos(direction) * range
            ));
        }
    }


}




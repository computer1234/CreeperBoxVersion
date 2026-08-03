package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PostMoveEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.mc.PlayerUtil;


@ModuleInfo(name = "安全行走", category = Category.Movement)
public class SafeWalk extends Module {

    private final BooleanValue airSafe = new BooleanValue("边缘行走",this,true);


    @SubscribeEvent
    public void onMove(PostMoveEvent event){
        EntityLocalPlayer player = event.getPlayer();

        boolean airSafe = this.airSafe.getCurrentValue();
        float yOff = -1;
        float airExpand = airSafe?10:0;

        AxisAlignedBB aabb = player.getAABB();
        Vec3f motion = player.getMotion();

        float motionX = motion.x;
        float motionZ = motion.z;

        if(PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(motionX,yOff,motionZ)).isEmpty() && PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(0,yOff,0).expand(0,airSafe?airExpand:0,0)).isEmpty()){
            return;
        }

        float delta = 0.05f;

        for(;motionX!=0 && PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(motionX,yOff,0).expand(0,airSafe?airExpand:0,0)).isEmpty();){
            if (motionX < delta && motionX >= -delta) {
                motionX = 0;
            } else if (motionX > 0) {
                motionX -= delta;
            } else {
                motionX += delta;
            }
        }


        for(;motionZ!=0 && PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(0,yOff,motionZ).expand(0,airSafe?airExpand:0,0)).isEmpty();){
            if (motionZ < delta && motionZ >= -delta) {
                motionZ = 0;
            } else if (motionZ > 0) {
                motionZ -= delta;
            } else {
                motionZ += delta;
            }
        }


        for(;motionX!=0 && motionZ!=0 && PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(motionX,yOff,motionZ).expand(0,airSafe?airExpand:0,0)).isEmpty();){
            if(motionX < delta  && motionX >= -delta){
                motionX = 0;
            }else if(motionX > 0){
                motionX -= delta;
            }else{
                motionX += delta;
            }
            if(motionZ < delta  && motionZ >= -delta){
                motionZ = 0;
            }else if(motionZ > 0){
                motionZ -= delta;
            }else{
                motionZ += delta;
            }
        }
        player.setMotion(new Vec3f(motionX,motion.y,motionZ));

    }



    @Override
    public String getTag() {
        return airSafe.getCurrentValue() ? "Air Safe" : "";
    }

}

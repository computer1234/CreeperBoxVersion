package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;

@ModuleInfo(name = "踏空", category = Category.Movement)
public class AirJump extends Module {


    @Override
    public void onEnable() {
        lastJump = false;
    }

    private boolean lastJump;
    @SubscribeEvent
    public void onTick(TickEvent event) {
        EntityLocalPlayer player = event.getPlayer();

        boolean jump = player.getMoveInput().isJumpDown;
        if (!lastJump && jump) {
            if (!player.isOnGround()) {
                Vec3f motion = player.getMotion();
                player.setMotion(new Vec3f(motion.x, 0.42f, motion.z));
            }
        }

        lastJump = jump;
    }



}

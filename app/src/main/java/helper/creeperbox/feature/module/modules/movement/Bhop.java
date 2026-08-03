package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.feature.event.EventPriority;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.component.MoveInputComponent;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;

@ModuleInfo(name = "兔子跳", category = Category.Movement)
public class Bhop extends Module {

    private final NumberValue speed = new NumberValue("速度", this,3,0, 10, 0.1);
    private final NumberValue hopSpeed = new NumberValue("高度", this,0.42,0, 2, 0.01);


    @SubscribeEvent(EventPriority.LOW)
    public void onTick(TickEvent event) {
        EntityLocalPlayer player = event.getPlayer();
        Vec3f motion = player.getMotion();
        MoveInputComponent moveInput = player.getMoveInput();
        float yaw = player.getRotation().y;

        float calcYaw = (yaw + 90) * (float)(3.1415926 / 180);
        float s = (float) Math.sin(calcYaw);
        float c = (float) Math.cos(calcYaw);
        float forward = moveInput.moveForward;
        float side = moveInput.moveSide;
        float speed = this.speed.getCurrentValue().floatValue();
        float moveX = (forward * c + side * s) * speed * 0.1f;
        float moveZ = (forward * s - side * c) * speed * 0.1f;
        float motionY = motion.y;


        if(player.isOnGround()){
            motionY = hopSpeed.getCurrentValue().floatValue();
        }

        player.setMotion(new Vec3f(moveX,motionY,moveZ));
    }



}

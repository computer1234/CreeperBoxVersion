package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.component.MoveInputComponent;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;


@ModuleInfo(name = "飞船", category = Category.Movement)
public class BoatFly extends Module {

    private final NumberValue speed = new NumberValue("速度", this,10,0, 10, 0.1);

    @SubscribeEvent
    public void onMove(TickEvent event) {

        EntityLocalPlayer player = event.getPlayer();
        EntityActor vehicle = player.getVehicle();
        if(vehicle!=null) {
            MoveInputComponent moveInput = player.getMoveInput();
            if(moveInput.moveSide!=0 || moveInput.moveForward !=0){
                float yaw = player.getRotation().y;
                float calcYaw = (yaw + 90.0F) * ((float)Math.PI / 180F);
                float s = (float)Math.sin((double)calcYaw);
                float c = (float)Math.cos((double)calcYaw);
                float forward = moveInput.moveForward;
                float side = moveInput.moveSide;
                float speed = ((Number)this.speed.getCurrentValue()).floatValue();
                float moveX = (forward * c + side * s) * speed;
                float moveZ = (forward * s - side * c) * speed;
                Vec2f rot = player.getRotation();
                float motionY = (speed * (float) (Math.sin(Math.toRadians(-rot.x))));
                vehicle.setMotion(new Vec3f(moveX,motionY,moveZ));
            }
        }

    }
}

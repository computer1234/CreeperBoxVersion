package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;

@ModuleInfo(name = "冲刺背包", category = Category.Movement)
public class SprintPack extends Module {
    private final NumberValue speed = new NumberValue("速度", this,3,0, 10, 0.1);

    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player!=null){
            Vec2f rot = player.getRotation();
            float rotx = rot.y;
            float roty = rot.x;
            float speed = this.speed.getCurrentValue().floatValue();

            player.setMotion(new Vec3f(
                    (speed * (float) (-Math.sin(Math.toRadians((rotx < 0) ? rotx + 360.f : rotx)) * Math.cos(Math.toRadians(-roty)))),
                    (speed * (float) (Math.sin(Math.toRadians(-roty)))),
                    (speed * (float) (Math.cos(Math.toRadians((rotx < 0) ? rotx + 360 : rotx)) * Math.cos(Math.toRadians(-roty))))
            ));
        }
    }
}

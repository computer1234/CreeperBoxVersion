package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.SetCameraEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;

@ModuleInfo(name = "运动摄像机", category = Category.Render)
public class MotionCamera extends Module {
    private boolean reset = false;

    public static Vec3f currentPos;

    @Override
    public void onEnable() {
        reset = true;
    }


    private boolean firstPer;
    private boolean lastPer;

    @SubscribeEvent
    public void onTick(TickEvent event) {
        lastPer = firstPer;
        firstPer = EntityLocalPlayer.getView()==0;
    }


    @Override
    public void onDisable() {
        currentPos = null;
    }

    @SubscribeEvent
    public void onCamera(SetCameraEvent event){
        if(!isEnable()) return;

        if(firstPer) {
            currentPos = null;
            return;
        }if(lastPer){
            reset = true;
        }

        if(reset){
            reset = false;
            currentPos = event.cameraPos;
        }else{
            Vec3f toPos = event.cameraPos;
            currentPos = new Vec3f(
                    currentPos.x+(toPos.x-currentPos.x)*0.02f,
                    currentPos.y+(toPos.y-currentPos.y)*0.02f,
                    currentPos.z+(toPos.z-currentPos.z)*0.02f
            );
            event.cameraPos = currentPos;
        }
    }

}

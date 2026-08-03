package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.sdk.component.MoveInputComponent;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "强制疾跑", category = Category.Movement)
public class KeepSprint extends Module {

    private final BooleanValue omni = new BooleanValue("全方位",this,false);

    public KeepSprint(){
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        MoveInputComponent moveInput = player.getMoveInput();
        if(!player.isSprinting() && (moveInput.moveForward!=0 || moveInput.moveSide!=0)) {
            if (!omni.getCurrentValue() && moveInput.moveForward <= 0.8) {
                return;
            }
            player.setSprinting(true);
        }
    }


}

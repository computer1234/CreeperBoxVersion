package helper.creeperbox.feature.module.modules.movement;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;

@ModuleInfo(name = "飞行绕过", category = Category.Movement)
public class Disabler extends Module {

    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            ((NeteasePlayerAuthInputPacket) event.getPacket()).setCameraDeparted(true);
        }
    }


}

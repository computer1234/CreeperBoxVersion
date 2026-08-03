package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;

@ModuleInfo(name = "屏蔽发言", category = Category.Survival)
public class AntiChat extends Module {
    @SubscribeEvent
    public void onReceive(PacketReceiveEvent event){
        if(event.getPacket() instanceof NeteaseTextPacket) event.setCancelled();
    }
}

package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;

@ModuleInfo(name = "停止发包", category = Category.Survival)
public class StopPacket extends Module {
    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        event.setCancelled();
    }
}


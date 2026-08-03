package helper.creeperbox.feature.module.modules.survival;


import org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;

@ModuleInfo(name = "防踢", category = Category.Survival)
public class AntiKick extends Module {
    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacket() instanceof DisconnectPacket) {
            event.setCancelled();
        }
    }
}

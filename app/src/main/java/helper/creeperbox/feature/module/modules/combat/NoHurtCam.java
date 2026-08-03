package helper.creeperbox.feature.module.modules.combat;

import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType;
import org.cloudburstmc.protocol.bedrock.packet.EntityEventPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "受击无抖动", category = Category.Combat)
public class NoHurtCam extends Module {


    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event){
        if(event.getPacket() instanceof EntityEventPacket){
            EntityEventPacket packet = (EntityEventPacket) event.getPacket();
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player != null && packet.getType() == EntityEventType.HURT && packet.getRuntimeEntityId() == player.getRuntimeID()){
                event.setCancelled();
            }
        }
    }



}

package helper.creeperbox.feature.module.modules.movement;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;

@ModuleInfo(name = "摔落无伤", category = Category.Movement)
public class NoFall extends Module {


    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            ((NeteasePlayerAuthInputPacket) event.getPacket()).setReadyPosDetalDirty(true);
        }
    }


}

package helper.creeperbox.feature.module.modules.survival;

import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;

@ModuleInfo(name = "灵魂出窍", category = Category.Survival)
public class FreeCam extends Module {

    private Vec3f lastPos;
    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null){
            lastPos = player.getPos();
        }
    }


    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket || event.getPacket() instanceof MovePlayerPacket){
            event.setCancelled(true);
        }
    }


    @Override
    public void onDisable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null){
            player.setPos(lastPos);
        }
    }


}

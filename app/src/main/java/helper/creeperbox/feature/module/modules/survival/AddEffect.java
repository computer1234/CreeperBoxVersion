package helper.creeperbox.feature.module.modules.survival;


import org.cloudburstmc.protocol.bedrock.packet.MobEffectPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "药水效果", category = Category.Survival)
public class AddEffect extends Module {
    @Override
    public void onDisable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null) {
            MobEffectPacket packet = new MobEffectPacket();
            packet.setAmplifier(0);
            packet.setDuration(Integer.MAX_VALUE);
            packet.setEvent(MobEffectPacket.Event.REMOVE);
            packet.setParticles(false);
            packet.setTick(0);
            packet.setRuntimeEntityId(player.getRuntimeID());
            packet.setEffectId(16);
            player.receivePacket(packet);
        }
    }




    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null) {
            MobEffectPacket packet = new MobEffectPacket();
            packet.setAmplifier(0);
            packet.setDuration(Integer.MAX_VALUE);
            packet.setEvent(MobEffectPacket.Event.ADD);
            packet.setParticles(false);
            packet.setTick(0);
            packet.setRuntimeEntityId(player.getRuntimeID());
            packet.setEffectId(16);
            player.receivePacket(packet);
        }

    }

}

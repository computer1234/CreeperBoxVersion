package helper.creeperbox.feature.module.modules.render;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteaseAddEntityPacket;

@ModuleInfo(name = "攻击特效", category = Category.Render)
public class KillEffect extends Module {

    @SubscribeEvent
    public void onAttack(PacketSendEvent event){
        if(event.getPacket() instanceof InventoryTransactionPacket){
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            if(packet.getTransactionType() == InventoryTransactionType.ITEM_USE_ON_ENTITY && packet.getActionType() == 1){
                EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                long id = packet.getRuntimeEntityId();
                for(EntityActor actor : player.getLevel().getPlayers()){
                    if(actor.getRuntimeID() == id){
                        NeteaseAddEntityPacket add = new NeteaseAddEntityPacket();
                        add.setIdentifier("minecraft:lightning_bolt");
                        Vec3f pos = actor.getPos();
                        add.setPosition(Vector3f.from(pos.x,pos.y-1.62f,pos.z));
                        add.setMotion(Vector3f.ZERO);
                        add.setRotation(Vector2f.ZERO);
                        player.receivePacket(add);
                        LevelSoundEventPacket sound = new LevelSoundEventPacket();
                        sound.setPosition(packet.getPlayerPosition());
                        sound.setBabySound(false);
                        sound.setExtraData(-1);
                        sound.setRelativeVolumeDisabled(true);
                        sound.setSound(SoundEvent.EXPLODE);
                        sound.setIdentifier("random.explode");
                        player.receivePacket(sound);
                        return;
                    }
                }
            }
        }
    }


}

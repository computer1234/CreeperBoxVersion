package helper.creeperbox.feature.module.modules.combat;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.KeyEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;

@ModuleInfo(name = "刀刀重锤", category = Category.Combat)
public class InfiniteMace extends Module {
    private final NumberValue height = new NumberValue("高度", this,1000,20, 4000, 20);

    @Override
    public void onEnable() {
        lastAuth = null;
    }

    private NeteasePlayerAuthInputPacket lastAuth = null;


    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event){
        if(event.getPacket() instanceof SetEntityMotionPacket){
            SetEntityMotionPacket packet = (SetEntityMotionPacket) event.getPacket();
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player!=null && packet.getRuntimeEntityId() == player.getRuntimeID()){
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketSendEvent event){

        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            lastAuth = (NeteasePlayerAuthInputPacket) event.getPacket();
            lastAuth.setReadyPosDetalDirty(true);
        }

        if(event.getPacket() instanceof InventoryTransactionPacket){
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            if(packet.getTransactionType() == InventoryTransactionType.ITEM_USE_ON_ENTITY) {

                EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                if(player == null) return;
                if(!player.getItemInHand().getNameSpace().equals("item.mace")) return;
                lastAuth.setReadyPosDetalDirty(false);
                Vector3f tempPos = lastAuth.getPosition();
                lastAuth.setCameraDeparted(true);
                int h = height.getCurrentValue().intValue();
                int count = h / 20;
                for(int i = 0 ; i<= count ; i ++){
                    lastAuth.setPosition(tempPos.add(0,h-i*20f,0));
                    player.sendPacketNoEvent(lastAuth);
                }
                event.setCancelled();
                player.sendPacketNoEvent(packet);
                lastAuth.setReadyPosDetalDirty(true);
                player.sendPacketNoEvent(lastAuth);
            }
        }
    }

}

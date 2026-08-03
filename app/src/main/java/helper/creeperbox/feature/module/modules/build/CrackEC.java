package helper.creeperbox.feature.module.modules.build;

import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket;

import java.io.IOException;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.network.packet.PyRpcPacket;

@ModuleInfo(name = "EC无限点券", category = Category.Build)
public class CrackEC extends Module {
    private InventoryTransactionPacket attackPacket;

    @SubscribeEvent
    public void onTick(TickEvent event){
        if(attackPacket!=null)
        {
//            for(int i = 0 ; i < 5 ; i ++){
                event.getPlayer().sendPacketNoEvent(attackPacket);
//            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if(event.getPacket() instanceof RemoveEntityPacket) {
            event.setCancelled(true);
        }
    }
    @SubscribeEvent
    public void onAttack(PacketSendEvent event){
        if(event.getPacket() instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            if (packet.getTransactionType() == InventoryTransactionType.ITEM_USE_ON_ENTITY && packet.getActionType() == 0) {
                attackPacket = packet;
            }
        }
    }

}

package helper.creeperbox.feature.module.modules.combat;

import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "爆发连击", category = Category.Combat)
public class CpsBoost extends Module {

    private final NumberValue multiple = new NumberValue("倍数", this,10,1, 100, 1);

    @SubscribeEvent
    public void onAttack(PacketSendEvent event){
        if(event.getPacket() instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            if (packet.getTransactionType() == InventoryTransactionType.ITEM_USE_ON_ENTITY && packet.getActionType() == 1) {
                int m = multiple.getCurrentValue().intValue();
                EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                if(player == null) return;
                for(int i = 0 ; i< m; i++){
                    player.sendPacketNoEvent(packet);
                }
            }
        }
    }


}

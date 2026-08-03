package helper.creeperbox.feature.module.modules.movement;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;

@ModuleInfo(name = "点击传送", category = Category.Movement)
public class ClickTeleport extends Module {


    @SubscribeEvent
    public void onSend(PacketSendEvent event){
        if (event.getPacket() instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            if(packet.getTransactionType() == InventoryTransactionType.ITEM_USE && packet.getActionType() == 0){
                EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                if(player==null) return;
                Vector3i pos = packet.getBlockPosition();
                player.setPos(new Vec3f(pos.getX(),pos.getY()+2.63f,pos.getZ()));
            }
        }
    }


}

package helper.creeperbox.feature.module.modules.build;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.BlockDestroyEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.inventory.PlayerInventory;
import helper.creeperbox.sdk.math.Vec3i;


@ModuleInfo(name = "自动工具", category = Category.Build)
public class AutoTool extends Module {

    private final BooleanValue spoof = new BooleanValue("欺骗客户端",this,true);

    private BestResult bestResult;

    private Vec3i breakPos;

    private BestResult findBestTool(EntityLocalPlayer player, Block block){
        int hand = player.getInventory().getSelected();
        PlayerInventory inventory = player.getInventory();
        BestResult result = new BestResult(-1,player.getDestroyRate(block));
        for(int i = 0 ; i <9 ; i ++){
            if(i != hand){
                inventory.setSelected(i);
                float value = player.getDestroyRate(block);
                if(result.getValue()<value){
                    result = new BestResult(i,value);
                }
            }
        }
        inventory.setSelected(hand);
        return result;
    }

    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        if(event.getPacket() instanceof PlayerActionPacket){
            PlayerActionPacket packet = (PlayerActionPacket) event.getPacket();

            if(packet.getAction() == PlayerActionType.START_BREAK || packet.getAction() == PlayerActionType.CONTINUE_BREAK){
                if(bestResult != null && breakPos != null){
                    Vector3i blockPos = packet.getBlockPosition();
                    if(blockPos.getX() == breakPos.x && blockPos.getY() == breakPos.y && blockPos.getZ() == breakPos.z){
                        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                        if(player!=null && bestResult.getSlot() != -1){
                            event.setCancelled();

                            int hand = player.getInventory().getSelected();
                            MobEquipmentPacket switchPacket = new MobEquipmentPacket();
                            switchPacket.setContainerId(0);
                            switchPacket.setHotbarSlot(bestResult.slot);
                            switchPacket.setInventorySlot(bestResult.slot);
                            switchPacket.setRuntimeEntityId(player.getRuntimeID());
                            switchPacket.setItem(player.getInventory().getContainer().getItemStack(bestResult.slot).getItemData());
                            player.sendPacket(switchPacket);

                            player.sendPacketNoEvent(packet);

                            MobEquipmentPacket switchPacket2 = new MobEquipmentPacket();
                            switchPacket2.setContainerId(0);
                            switchPacket2.setHotbarSlot(hand);
                            switchPacket2.setInventorySlot(hand);
                            switchPacket2.setRuntimeEntityId(player.getRuntimeID());
                            switchPacket2.setItem(player.getInventory().getContainer().getItemStack(hand).getItemData());
                            player.sendPacket(switchPacket2);
                        }

                    }
                }
            }
        }

        if (event.getPacket() instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();

            //stop break
            if (packet.getActionType() == 2 && bestResult != null && breakPos != null) {

                Vector3i blockPos = packet.getBlockPosition();
                if(blockPos.getX() == breakPos.x && blockPos.getY() == breakPos.y && blockPos.getZ() == breakPos.z) {
                    EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                    if(player!=null && bestResult.getSlot() != -1){

                        event.setCancelled();

                        int hand = player.getInventory().getSelected();
                        MobEquipmentPacket switchPacket = new MobEquipmentPacket();
                        switchPacket.setContainerId(0);
                        switchPacket.setHotbarSlot(bestResult.slot);
                        switchPacket.setInventorySlot(bestResult.slot);
                        switchPacket.setRuntimeEntityId(player.getRuntimeID());
                        switchPacket.setItem(player.getInventory().getContainer().getItemStack(bestResult.slot).getItemData());
                        player.sendPacket(switchPacket);

                        packet.setHotbarSlot(bestResult.slot);
                        packet.setItemInHand(player.getInventory().getContainer().getItemStack(bestResult.slot).getItemData());
                        player.sendPacketNoEvent(packet);


                        MobEquipmentPacket switchPacket2 = new MobEquipmentPacket();
                        switchPacket2.setContainerId(0);
                        switchPacket2.setHotbarSlot(hand);
                        switchPacket2.setInventorySlot(hand);
                        switchPacket2.setRuntimeEntityId(player.getRuntimeID());
                        switchPacket2.setItem(player.getInventory().getContainer().getItemStack(hand).getItemData());
                        player.sendPacket(switchPacket2);
                    }
                }
            }
        }

    }



    @SubscribeEvent
    public void onDestroy(BlockDestroyEvent event){
        Block block = event.getBlock();
        EntityLocalPlayer player = event.getPlayer();


        if(!spoof.getCurrentValue()){
            BestResult best = findBestTool(player,block);
            if(best.getSlot()!=-1){
                player.getInventory().setSelected(best.getSlot());
            }
        }


        if(spoof.getCurrentValue()){
            BestResult best = findBestTool(player,block);

            if(best.getSlot()!=-1){
                bestResult = best;
                breakPos = block.getPos();
                event.setProgress(bestResult.getValue());
            }
        }

    }

    @Override
    public String getTag() {
        if(spoof.getCurrentValue()) return "Spoof";
        return "";
    }

    class BestResult {
        private int slot;
        private float value;

        public BestResult(int slot, float value) {
            this.slot = slot;
            this.value = value;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    };
}

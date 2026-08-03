package helper.creeperbox.feature.module.modules.combat;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.Attributes;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "自动金苹果", category = Category.Combat)
public class GApple extends Module {

    private final NumberValue health = new NumberValue("血量阈值", this,10,5, 15, 1);
    private final NumberValue speed = new NumberValue("速度", this,1.5,1, 10, 0.1);



    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        if(!eating){
            if(delayWatch.finished(speed.getCurrentValue().longValue()*1000) && player.getAttributeCurrentValue(Attributes.HEALTH)<health.getCurrentValue().intValue()){
                int selected = player.getInventory().getSelected();
                int slot = searchAppleInHotBar(player);
                if(slot != -1){

                    boolean needSwitch = selected!=slot;
                    ItemData item = player.getInventory().getContainer().getItemStack(slot).getItemData();

                    if(needSwitch){
                        MobEquipmentPacket packet = new MobEquipmentPacket();
                        packet.setContainerId(0);
                        packet.setHotbarSlot(slot);
                        packet.setInventorySlot(slot);
                        packet.setRuntimeEntityId(player.getRuntimeID());
                        packet.setItem(item);
                        player.sendPacket(packet);
                    }

                    usingItem(player,slot);

                    eatingCount = item.getCount();
                    eatingSlot = slot;
                    eatingWatch.reset();

                    eating = true;

                }
            }
        }else{

            if(eatingWatch.finished(2000) || player.getInventory().getContainer().getItemStack(eatingSlot).getItemData().getCount()!=eatingCount){

                if(!eatingWatch.finished(2000)){
                    player.displayClientMessage("§b§lCreeperBox Eating Apple");
                }

                int selected = player.getInventory().getSelected();
                boolean needSwitch = selected!=eatingSlot;

                eating = false;
                delayWatch.reset();

                releaseUsingItem(player,eatingSlot);

                if(needSwitch){
                    MobEquipmentPacket packet = new MobEquipmentPacket();
                    packet.setContainerId(0);
                    packet.setHotbarSlot(selected);
                    packet.setInventorySlot(selected);
                    packet.setRuntimeEntityId(player.getRuntimeID());
                    packet.setItem(player.getItemInHand().getItemData());
                    player.sendPacket(packet);
                }

            }
        }


    }

    private boolean eating;

    private StopWatch eatingWatch = new StopWatch();

    private StopWatch delayWatch = new StopWatch();
    private int eatingCount;

    private int eatingSlot;
    private int searchAppleInHotBar(EntityLocalPlayer player){
        String handName = player.getItemInHand().getNameSpace();
        if(handName.contains("golden_apple") || handName.contains("appleEnchanted")){
            return player.getInventory().getSelected();
        }
        for(int i = 0 ; i< 9 ; i ++){
            String itemName = player.getInventory().getContainer().getItemStack(i).getNameSpace();
            if(itemName.contains("golden_apple") || itemName.contains("appleEnchanted")){
                return i;
            }
        }
        return -1;
    }



    @Override
    public void onEnable() {
        eating = false;
        eatingWatch.reset();
        delayWatch.reset();
    }


    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event){
        if(event.getPacket() instanceof MobEquipmentPacket && eating){
            event.setCancelled();
        }

        if(event.getPacket() instanceof InventoryTransactionPacket && eating){
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();

            //Only Handle Attack Packet
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player != null && packet.getTransactionType() == InventoryTransactionType.ITEM_USE_ON_ENTITY && packet.getActionType() == 1 ){
                packet.setHotbarSlot(eatingSlot);
                packet.setItemInHand(player.getInventory().getContainer().getItemStack(eatingSlot).getItemData());
            }else{
                event.setCancelled();
            }
        }

    }


    public static void usingItem(EntityLocalPlayer player,int slot){
        Vec3f pos = player.getPos();
        InventoryTransactionPacket packet = new InventoryTransactionPacket();
        packet.setClickPosition(Vector3f.ZERO);
        packet.setRuntimeEntityId(0);
        packet.setPlayerPosition(Vector3f.from(pos.x,pos.y,pos.z));
        packet.setTransactionType(InventoryTransactionType.ITEM_USE);
        packet.setBlockPosition(Vector3i.ZERO);
        packet.setBlockFace(255);
        packet.setActionType(1);
        packet.setHotbarSlot(slot);
        ItemData data = player.getInventory().getContainer().getItemStack(slot).getItemData();
        packet.setItemInHand(data);
        packet.setBlockDefinition(data.getBlockDefinition());
        player.sendPacket(packet);
    }


    public static void releaseUsingItem(EntityLocalPlayer player,int slot){
        Vec2f rot = player.getRotation();
        InventoryTransactionPacket release = new InventoryTransactionPacket();
        release.setTransactionType(InventoryTransactionType.ITEM_RELEASE);
        release.setHeadPosition(Vector3f.from(rot.x,rot.y, rot.y));
        ItemData data = player.getInventory().getContainer().getItemStack(slot).getItemData();
        release.setItemInHand(data);
        release.setHotbarSlot(slot);
        player.sendPacket(release);
    }


}

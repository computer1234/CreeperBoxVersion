package helper.creeperbox.feature.module.modules.survival;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.InteractPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.InventoryComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.KeyEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.network.inv.AbstractInventoryData;
import helper.creeperbox.network.inv.PlayerInventoryData;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.ItemDef;
import helper.creeperbox.sdk.network.ItemTags;
import helper.creeperbox.sdk.network.packet.modify.NeteaseContainerOpenPacket;
import helper.creeperbox.utils.mc.ItemUtil;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "背包整理", category = Category.Survival,key = KeyEvent.KEYCODE_C)
public class InvManager extends Module {


    private final NumberValue delay = new NumberValue("延迟", this,0, 0, 500, 50);

    private final BooleanValue silent = new BooleanValue("静默",this,true);


    private boolean hasSimulated = false;
    private boolean realOpen = false;
    private final StopWatch stopwatch = new StopWatch();
    private int moveTick = 0;
    private StopWatch simWatch = new StopWatch();

    public InvManager(){
    }

    @Override
    public void onEnable() {
        realOpen = false;
        moveTick = 0;
        stopwatch.reset();
        hasSimulated = false;
        simWatch.reset();
    }


    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        if(player.getGameType() == 2 ) return;
        if(!(InventoryComponent.getOpenContainer() instanceof PlayerInventoryData)){
            if(!silent.getCurrentValue()){
                return;
            }else {
                if(InventoryComponent.getOpenContainer()!=null) return;
                if(hasSimulated && simWatch.finished(1000)){
                    InteractPacket packet = new InteractPacket();
                    packet.setRuntimeEntityId(player.getRuntimeID());
                    packet.setAction(InteractPacket.Action.OPEN_INVENTORY);
                    packet.setMousePosition(Vector3f.ZERO);
                    player.sendPacket(packet);
                    simWatch.reset();
                }
            }
        }

        int block = -1;
        int blockCount = 0;

        int food = -1;
        int foodCount = 0;


        boolean hasMove = false;
        AbstractInventoryData invData = InventoryComponent.getPlayerInv();
        if(!stopwatch.finished(moveTick)) return;
        for(int i = 0; i < invData.getSize(); i++) {

            if(hasMove){
                stopwatch.reset();
                moveTick = (int) delay.getCurrentValue();
                if(moveTick != 0){
                    InventoryComponent.sendActions(player);
                    return;
                }
            }

            ItemData item = invData.getItem(i);
            if (item == null) {
                continue;
            }


            if (item == ItemData.AIR) {
                continue;
            }

            if (!(item.getDefinition() instanceof ItemDef)) {
                continue;
            }

            ItemDef def = (ItemDef) item.getDefinition();

            if(!ItemUtil.isUseFul(item,def)){
                doDrop(player,invData,i);
                hasMove = true;
                continue;
            }

            if(InventoryComponent.hadBetterItem(item,def,i,invData)){
                doDrop(player,invData,i);
                hasMove = true;
                continue;
            }

            if(def.tags.contains(ItemTags.TAG_IS_SWORD)){
                int slot = 1;
                if(i == slot){
                    continue;
                }
                doMoveOrSwap(player,invData,i,invData,slot);
                hasMove = true;
                continue;
            }



            if(ItemUtil.isBlock(item)){
                if(blockCount < item.getCount()){
                    block = i;
                    blockCount = item.getCount();
                }
            }

            if(def.tags.contains(ItemTags.TAG_IS_FOOD)) {
                int count = item.getCount();

                if(def.name.equals("minecraft:golden_apple")){
                    count+= 64;
                }

                if(def.name.equals("minecraft:enchanted_golden_apple")){
                    count+= 128;
                }

                if(foodCount < count){
                    food = i;
                    foodCount = count;
                }

            }

            if(def.tags.contains(ItemTags.TAG_IS_HELMET)) {
                if(i == 36) continue;
                doMoveOrSwap(player,invData,i,invData,36);
                hasMove = true;
                continue;
            }

            if(def.tags.contains(ItemTags.TAG_IS_CHESTPLATE)) {
                if(i == 37) continue;
                doMoveOrSwap(player,invData,i,invData,37);
                hasMove = true;
                continue;
            }

            if(def.tags.contains(ItemTags.TAG_IS_LEGGINGS)) {
                if(i == 38) continue;
                doMoveOrSwap(player,invData,i,invData,38);
                hasMove = true;
                continue;
            }

            if(def.tags.contains(ItemTags.TAG_IS_BOOTS)) {
                if(i == 39) continue;
                doMoveOrSwap(player,invData,i,invData,39);
                hasMove = true;
            }
        }

        int blockS = 0;
        if(block >= 0 && block != blockS){
            if(!ItemUtil.isBlock(invData.getItem(blockS))){
                if(hasMove){
                    stopwatch.reset();
                    moveTick = delay.getCurrentValue().intValue();
                    if(moveTick != 0){
                        InventoryComponent.sendActions(player);
                        return;
                    }
                }
                doMoveOrSwap(player,invData,block,invData,blockS);
                hasMove = true;
            }
        }


        int foodS = 2;
        if(food >= 0 && food != foodS){
            if(hasMove){
                stopwatch.reset();
                moveTick = delay.getCurrentValue().intValue();
                if(moveTick != 0){
                    InventoryComponent.sendActions(player);
                    return;
                }
            }
            doMoveOrSwap(player,invData,food,invData,foodS);
        }

        InventoryComponent.sendActions(player);

        if(!realOpen && silent.getCurrentValue() && InventoryComponent.getOpenContainer() instanceof PlayerInventoryData){
            ContainerClosePacket packet = new ContainerClosePacket();
            packet.setServerInitiated(true);
            packet.setId((byte)0);
            packet.setType(ContainerType.INVENTORY);
            player.sendPacket(packet);
        }
    }


    private void doDrop(EntityLocalPlayer player,AbstractInventoryData inv,int slot){
        if(silent.getCurrentValue() && !(InventoryComponent.getOpenContainer() instanceof PlayerInventoryData)) {
            if(!hasSimulated){
                hasSimulated = true;
                InteractPacket packet = new InteractPacket();
                packet.setRuntimeEntityId(player.getRuntimeID());
                packet.setAction(InteractPacket.Action.OPEN_INVENTORY);
                packet.setMousePosition(Vector3f.ZERO);
                player.sendPacket(packet);
                simWatch.reset();
            }
        }else{
            InventoryComponent.drop(player, inv, slot);
        }
    }


    private void doMoveOrSwap(EntityLocalPlayer player, AbstractInventoryData from, int fromSlot, AbstractInventoryData dest, int destSlot){
        if(silent.getCurrentValue() && !(InventoryComponent.getOpenContainer() instanceof PlayerInventoryData)) {
            if(!hasSimulated){
                hasSimulated = true;
                InteractPacket packet = new InteractPacket();
                packet.setRuntimeEntityId(player.getRuntimeID());
                packet.setAction(InteractPacket.Action.OPEN_INVENTORY);
                packet.setMousePosition(Vector3f.ZERO);
                player.sendPacket(packet);
                simWatch.reset();
            }
        }else{
            InventoryComponent.moveOrSwap(player, from, fromSlot, dest, destSlot);
        }
    }

    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event){
        if(event.getPacket() instanceof NeteaseContainerOpenPacket){
            NeteaseContainerOpenPacket packet = (NeteaseContainerOpenPacket) event.getPacket();
            if(hasSimulated && packet.getId() == 0){
                hasSimulated = false;
                event.setCancelled();
            }
        }

        if(event.getPacket() instanceof ContainerClosePacket){
            ContainerClosePacket packet = (ContainerClosePacket) event.getPacket();
            if(packet.getId() == 0 && realOpen){
                realOpen = false;
            }
        }
    }


    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(!hasSimulated && player!= null && event.getPacket() instanceof InteractPacket && silent.getCurrentValue()){
            InteractPacket packet = (InteractPacket) event.getPacket();
            if(packet.getAction() == InteractPacket.Action.OPEN_INVENTORY){
                realOpen = true;
                if(InventoryComponent.getOpenContainer() instanceof PlayerInventoryData){
                    NeteaseContainerOpenPacket packet1 = new NeteaseContainerOpenPacket();
                    packet1.setId((byte) 0);
                    packet1.setUniqueEntityId(player.getEntityID());
                    packet1.setType(ContainerType.INVENTORY);
                    packet1.setBlockPosition(Vector3i.ZERO);
                    player.receivePacketNoEvent(packet1);
                    event.setCancelled();
                }
            }
        }
    }

}

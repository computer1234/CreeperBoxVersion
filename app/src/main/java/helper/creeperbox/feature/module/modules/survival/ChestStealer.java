package helper.creeperbox.feature.module.modules.survival;

import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;

import java.util.List;

import helper.creeperbox.feature.component.InventoryComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.KeyEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.network.inv.ContainerInventoryData;
import helper.creeperbox.network.inv.PlayerInventoryData;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.ItemDef;
import helper.creeperbox.sdk.network.packet.modify.NeteaseContainerOpenPacket;
import helper.creeperbox.utils.mc.ItemUtil;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "箱子取物", category = Category.Survival,key = KeyEvent.KEYCODE_C)
public class ChestStealer extends Module {

    private final NumberValue delay = new NumberValue("延迟", this,0, 0, 500, 50);
    private final StopWatch stopwatch = new StopWatch();
    private int stealTick;
    @Override
    public void onEnable() {
        stealTick = 0;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event){
        if(event.getPacket() instanceof NeteaseContainerOpenPacket){
            NeteaseContainerOpenPacket packet = (NeteaseContainerOpenPacket) event.getPacket();
            if(packet.getType() == ContainerType.CONTAINER && packet.getId() != 0 && packet.getId() != 120 && packet.getId() != 121){
                event.setCancelled();
            }
        }
    }


    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        if (InventoryComponent.getOpenContainer() instanceof ContainerInventoryData) {

            if(!stopwatch.finished(stealTick)) return;

            boolean hasSteal = false;

            ContainerInventoryData chestData = (ContainerInventoryData) InventoryComponent.getOpenContainer();
            PlayerInventoryData invData = InventoryComponent.getPlayerInv();
            if(chestData.contents==null) return;

            for(int i = 0; i < chestData.getSize(); i++){

                if(hasSteal){
                    stopwatch.reset();
                    stealTick = delay.getCurrentValue().intValue();
                    if(stealTick != 0){
                        InventoryComponent.sendActions(player);
                        return;
                    }
                }

                ItemData item = chestData.getItem(i);

                if(item == null){
                    continue;
                }

                if(item == ItemData.AIR){
                    continue;
                }

                if(!(item.getDefinition() instanceof ItemDef)){
                    continue;
                }

                ItemDef def = (ItemDef) item.getDefinition();

                if(!ItemUtil.isUseFul(item,def)){
                    continue;
                }

                if((InventoryComponent.hadBetterItem(item,def,i,chestData) || InventoryComponent.hadBetterItem(item,def,-1,InventoryComponent.getPlayerInv()))){
                    continue;
                }

                List<Integer> canStackSlot = InventoryComponent.findItemSlot(item,def,invData);
                for(int s : canStackSlot){
                    ItemData slotItem = invData.getItem(s);
                    if(slotItem.getDefinition() instanceof ItemDef){
                        ItemDef slotData = (ItemDef) slotItem.getDefinition();
                        int maxSize = slotData.maxSize;
                        if(item.getCount()<=0) break;
                        if(maxSize>slotItem.getCount()) {
                            InventoryComponent.merge(player,chestData,i,Math.min(maxSize-slotItem.getCount(),item.getCount()),invData,s);
                            hasSteal = true;
                        }
                    }
                }

                if(chestData.getItem(i).getCount()<=0) continue;

                int emptySlot = InventoryComponent.findEmptySlot(invData);
                if(emptySlot == -1){
                    break;
                }

                InventoryComponent.moveOrSwap(player, chestData,i,invData,emptySlot);
                hasSteal = true;

            }

            if(hasSteal){
                stopwatch.reset();
                stealTick = delay.getCurrentValue().intValue();
                InventoryComponent.sendActions(player);
            }

            ContainerClosePacket packet = new ContainerClosePacket();
            packet.setId((byte)chestData.id);
            packet.setServerInitiated(true);
            packet.setType(ContainerType.CONTAINER);
            player.sendPacket(packet);

        }
    }


}

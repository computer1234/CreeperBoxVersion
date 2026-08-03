package helper.creeperbox.network.inv;

import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryData implements AbstractInventoryData {

    public List<ItemData> contents;
    public List<ItemData> armors;
    public ItemData offhand;
    public PlayerInventoryData(){
        armors = new ArrayList<>();
        for(int i = 0 ; i < 4 ;i ++){
            armors.add(ItemData.AIR);
        }
        offhand = ItemData.AIR;
    }
    @Override
    public ItemData getItem(int slot) {
        if(slot>=36 && slot<=39){
            return armors.get(slot-36);
        }
        if(slot == 40) return offhand;
        return contents==null?ItemData.AIR:((contents.size()<=slot || slot<0)?ItemData.AIR:contents.get(slot));
    }

    @Override
    public ContainerSlotType getType(int slot) {
        if(slot>=36 && slot<=39) return ContainerSlotType.ARMOR;
        if(slot == 40) return ContainerSlotType.OFFHAND;
        return ContainerSlotType.HOTBAR_AND_INVENTORY;
    }



    @Override
    public int getID(int slot) {
        if(slot>=36 && slot<=39) return ContainerId.ARMOR;
        if(slot == 40) return ContainerId.OFFHAND;
        return 0;
    }
    @Override
    public int getSlot(int slot) {
        if(slot>=36 && slot<=39) return slot-36;
        if(slot == 40) return 0;
        return slot;
    }


    @Override
    public int getSize() {
        return contents.size() + 5;
    }

    @Override
    public void setItem(int index, ItemData data) {
        if(index>=36 && index<=39){
            armors.set(index-36,data);
            return;
        }
        if(index == 40){
            offhand = data;
            return;
        }
        if(contents.size()<=index || index<0) return;
        contents.set(index,data);
    }




}

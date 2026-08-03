package helper.creeperbox.network.inv;

import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import java.util.List;

public class ContainerInventoryData implements AbstractInventoryData {
    public int id;
    public List<ItemData> contents;
    @Override
    public ItemData getItem(int slot) {
        return contents==null?ItemData.AIR:((contents.size()<=slot || slot<0)?ItemData.AIR:contents.get(slot));
    }

    @Override
    public ContainerSlotType getType(int slot) {
        return ContainerSlotType.LEVEL_ENTITY;
    }

    @Override
    public int getID(int slot) {
        return id;
    }

    @Override
    public int getSlot(int slot) {
        return slot;
    }


    @Override
    public int getSize() {
        return contents.size();
    }

    @Override
    public void setItem(int index, ItemData data) {
        if(contents.size()<=index || index<0) return;
        contents.set(index,data);
    }
}

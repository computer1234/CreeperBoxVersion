package helper.creeperbox.network.inv;

import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

public interface AbstractInventoryData {
    ItemData getItem(int slot);
    ContainerSlotType getType(int slot);
    int getID(int slot);
    int getSlot(int slot);
    int getSize();
    void setItem(int index,ItemData data);
}

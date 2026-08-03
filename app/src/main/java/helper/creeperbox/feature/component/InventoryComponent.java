package helper.creeperbox.feature.component;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.network.inv.AbstractInventoryData;
import helper.creeperbox.network.inv.ContainerInventoryData;
import helper.creeperbox.network.inv.PlayerInventoryData;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.ItemDef;
import helper.creeperbox.sdk.network.packet.modify.NeteaseContainerOpenPacket;
import helper.creeperbox.utils.mc.ItemUtil;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequestSlotData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DropAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.PlaceAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.SwapAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryActionData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.*;

import java.util.ArrayList;
import java.util.List;

public class InventoryComponent {

    private static AbstractInventoryData openContainer;
    private static PlayerInventoryData playerInv;
    private static ItemStackRequestPacket queue = null;
    private static final List<String> usefulTag = new ArrayList();

    public static AbstractInventoryData getOpenContainer() {
        return openContainer;
    }

    public static PlayerInventoryData getPlayerInv() {
        return playerInv;
    }

    public InventoryComponent() {
        playerInv = new PlayerInventoryData();
        playerInv.contents = new ArrayList();
    }

    @SubscribeEvent(5)
    public void onPacketSend(PacketSendEvent event) {

        if (event.getPacket() instanceof ContainerClosePacket) {
            ContainerClosePacket packet = (ContainerClosePacket)event.getPacket();
            if (openContainer != null && packet.getId() == openContainer.getID(0)) {
                openContainer = null;
            }
        }

        if (event.getPacket() instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket packet = (InventoryTransactionPacket)event.getPacket();
            if (packet.getTransactionType() != InventoryTransactionType.NORMAL) {
                return;
            }

            for(InventoryActionData data : packet.getActions()) {
                if (data.getSource().getType() == InventorySource.Type.CONTAINER) {
                    int id = data.getSource().getContainerId();
                    if (id == 0) {
                        int index = data.getSlot();
                        if (playerInv.contents == null || index < 0 || index > playerInv.contents.size()) {
                            return;
                        }

                        playerInv.contents.set(index, data.getToItem());
                    } else if (id == 119) {
                        playerInv.setItem(40, data.getToItem());
                    } else if (id == 120) {
                        playerInv.setItem(data.getSlot() + 36, data.getToItem());
                    } else if (openContainer != null && openContainer.getID(data.getSlot()) == id) {
                        openContainer.setItem(data.getSlot(), data.getToItem());
                    }
                }
            }
        }

        if (event.getPacket() instanceof ItemStackRequestPacket) {
            ItemStackRequestPacket packet = (ItemStackRequestPacket)event.getPacket();

            for(ItemStackRequest request : packet.getRequests()) {
                for(ItemStackRequestAction requestAction : request.getActions()) {
                    if (requestAction instanceof PlaceAction) {
                        PlaceAction action = (PlaceAction)requestAction;
                        AbstractInventoryData from = null;
                        AbstractInventoryData dest = null;
                        ItemStackRequestSlotData sourceData = action.getSource();
                        ItemStackRequestSlotData destData = action.getDestination();
                        if (openContainer != null) {
                            if (sourceData.getContainer() == openContainer.getType(sourceData.getSlot())) {
                                from = openContainer;
                            }

                            if (destData.getContainer() == openContainer.getType(destData.getSlot())) {
                                dest = openContainer;
                            }
                        }

                        if (playerInv != null) {
                            if (sourceData.getContainer() != ContainerSlotType.LEVEL_ENTITY) {
                                from = playerInv;
                            }

                            if (destData.getContainer() != ContainerSlotType.LEVEL_ENTITY) {
                                dest = playerInv;
                            }
                        }

                        if (from != null && dest != null) {
                            ItemData fromItem = from.getItem(sourceData.getSlot());
                            ItemData destItem = dest.getItem(destData.getSlot());
                            int count = action.getCount();
                            ItemData changedFrom = fromItem.getCount() - count <= 0 ? ItemData.AIR : fromItem.toBuilder().count(fromItem.getCount() - count).build();
                            ItemData changedDest = destItem == ItemData.AIR ? fromItem.toBuilder().count(count).build() : destItem.toBuilder().count(destItem.getCount() + count).build();
                            from.setItem(sourceData.getSlot(), changedFrom);
                            dest.setItem(destData.getSlot(), changedDest);
                        }
                    }

                    if (requestAction instanceof DropAction) {
                        DropAction action = (DropAction)requestAction;
                        AbstractInventoryData dropInv = null;
                        ItemStackRequestSlotData dropData = action.getSource();
                        if (openContainer != null && dropData.getContainer() == openContainer.getType(dropData.getSlot())) {
                            dropInv = openContainer;
                        }

                        if (playerInv != null && dropData.getContainer() != ContainerSlotType.LEVEL_ENTITY) {
                            dropInv = playerInv;
                        }

                        if (dropInv != null) {
                            ItemData dropItem = dropInv.getItem(dropData.getSlot());
                            int count = action.getCount();
                            ItemData changedItem = dropItem.getCount() - count <= 0 ? ItemData.AIR : dropItem.toBuilder().count(dropItem.getCount() - count).build();
                            dropInv.setItem(dropData.getSlot(), changedItem);
                        }
                    }

                    if (requestAction instanceof SwapAction) {
                        SwapAction action = (SwapAction)requestAction;
                        AbstractInventoryData from = null;
                        AbstractInventoryData dest = null;
                        ItemStackRequestSlotData sourceData = action.getSource();
                        ItemStackRequestSlotData destData = action.getDestination();
                        if (openContainer != null) {
                            if (sourceData.getContainer() == openContainer.getType(sourceData.getSlot())) {
                                from = openContainer;
                            }

                            if (destData.getContainer() == openContainer.getType(destData.getSlot())) {
                                dest = openContainer;
                            }
                        }

                        if (playerInv != null) {
                            if (sourceData.getContainer() != ContainerSlotType.LEVEL_ENTITY) {
                                from = playerInv;
                            }

                            if (destData.getContainer() != ContainerSlotType.LEVEL_ENTITY) {
                                dest = playerInv;
                            }
                        }

                        if (from != null && dest != null) {
                            ItemData fromItem = from.getItem(sourceData.getSlot());
                            ItemData destItem = dest.getItem(destData.getSlot());
                            from.setItem(sourceData.getSlot(), destItem);
                            dest.setItem(destData.getSlot(), fromItem);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(5)
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof NeteaseContainerOpenPacket) {
            NeteaseContainerOpenPacket packet = (NeteaseContainerOpenPacket)event.getPacket();
            if (packet.getType() == ContainerType.CONTAINER && packet.getId() != 0) {
                openContainer = new ContainerInventoryData();
                ((ContainerInventoryData)openContainer).id = packet.getId();
            }

            if (packet.getId() == 0) {
                openContainer = playerInv;
            }
        }

        if (event.getPacket() instanceof ContainerClosePacket) {
            ContainerClosePacket packet = (ContainerClosePacket)event.getPacket();
            if (openContainer != null && packet.getId() == openContainer.getID(0)) {
                openContainer = null;
            }
        }

        if (event.getPacket() instanceof InventoryContentPacket) {
            InventoryContentPacket packet = (InventoryContentPacket)event.getPacket();
            if (packet.getContainerId() == 0) {
                playerInv.contents = packet.getContents();
            } else if (packet.getContainerId() == 119) {
                playerInv.offhand = (ItemData)packet.getContents().get(0);
            } else if (packet.getContainerId() == 120) {
                playerInv.armors = packet.getContents();
            } else if (openContainer != null && openContainer.getID(0) == packet.getContainerId() && openContainer instanceof ContainerInventoryData) {
                ((ContainerInventoryData)openContainer).contents = packet.getContents();
            }
        }

        if (event.getPacket() instanceof InventorySlotPacket) {
            InventorySlotPacket packet = (InventorySlotPacket)event.getPacket();
            if (packet.getContainerId() == 0) {
                int index = packet.getSlot();
                if (playerInv.contents == null || index < 0 || index > playerInv.contents.size()) {
                    return;
                }
                playerInv.contents.set(index, packet.getItem());
            } else if (packet.getContainerId() == 119) {
                playerInv.setItem(40, packet.getItem());
            } else if (packet.getContainerId() == 120) {
                playerInv.setItem(packet.getSlot() + 36, packet.getItem());
            } else if (openContainer != null && openContainer.getID(packet.getSlot()) == packet.getContainerId()) {
                openContainer.setItem(packet.getSlot(), packet.getItem());
            }
        }

    }

    public static int findEmptySlot(AbstractInventoryData data) {
        for(int i = 0; i < data.getSize(); ++i) {
            if (data.getItem(i) == ItemData.AIR) {
                return i;
            }
        }
        return -1;
    }

    public static void sendActions(EntityLocalPlayer player) {
        if (queue != null) {
            player.sendPacketNoEvent(queue);
            queue = null;
        }
    }


    public static void checkQueueNull(){
        if(queue==null) queue = new ItemStackRequestPacket();
    }

    public static void merge(EntityLocalPlayer player, AbstractInventoryData from, int fromSlot, int count, AbstractInventoryData dest, int destSlot) {
        ItemData fromItem = from.getItem(fromSlot);
        ItemData destItem = dest.getItem(destSlot);
        ItemData fromChanged = fromItem.getCount() - count <= 0 ? ItemData.AIR : fromItem.toBuilder().count(fromItem.getCount() - count).build();
        ItemData destChanged = destItem.toBuilder().count(destItem.getCount() + count).build();
        if (GameDataComponent.inventoriesServerAuthoritative) {
            checkQueueNull();
            int requestID = CreeperBox.getNextRequestID();
            queue.getRequests().add(new ItemStackRequest(requestID, new ItemStackRequestAction[]{null,new PlaceAction(count, new ItemStackRequestSlotData(from.getType(fromSlot), from.getSlot(fromSlot), fromItem.getNetId(),null), new ItemStackRequestSlotData(dest.getType(destSlot), dest.getSlot(destSlot), destItem.getNetId(),null))}, new String[0], (TextProcessingEventOrigin)null));
            CreeperBox.setNextRequestID(requestID-2);
        } else {
            InventoryTransactionPacket packet = new InventoryTransactionPacket();
            packet.setTransactionType(InventoryTransactionType.NORMAL);
            packet.getActions().add(new InventoryActionData(InventorySource.fromContainerWindowId(from.getID(fromSlot)), fromSlot, fromItem, fromChanged));
            packet.getActions().add(new InventoryActionData(InventorySource.fromContainerWindowId(dest.getID(destSlot)), destSlot, destItem, destChanged));
            player.sendPacketNoEvent(packet);
        }

        InventorySlotPacket updatePacket = new InventorySlotPacket();
        updatePacket.setSlot(from.getSlot(fromSlot));
        updatePacket.setItem(fromChanged);
        updatePacket.setContainerId(from.getID(fromSlot));
        player.receivePacketNoEvent(updatePacket);
        InventorySlotPacket updatePacket2 = new InventorySlotPacket();
        updatePacket2.setSlot(dest.getSlot(destSlot));
        updatePacket2.setItem(destChanged);
        updatePacket2.setContainerId(dest.getID(destSlot));
        player.receivePacketNoEvent(updatePacket2);
        from.setItem(fromSlot, fromChanged);
        dest.setItem(destSlot, destChanged);
    }

    public static void drop(EntityLocalPlayer player, AbstractInventoryData inv, int slot) {
        ItemData dropItem = inv.getItem(slot);
        if (GameDataComponent.inventoriesServerAuthoritative) {
            checkQueueNull();
            int requestID = CreeperBox.getNextRequestID();
            queue.getRequests().add(new ItemStackRequest(requestID, new ItemStackRequestAction[]{new DropAction(dropItem.getCount(), new ItemStackRequestSlotData(inv.getType(slot), inv.getSlot(slot), dropItem.getNetId(),null), false)}, new String[0], (TextProcessingEventOrigin)null));
            CreeperBox.setNextRequestID(requestID-2);
        } else {
            InventoryTransactionPacket packet = new InventoryTransactionPacket();
            packet.setTransactionType(InventoryTransactionType.NORMAL);
            ItemData item = dropItem.toBuilder().usingNetId(false).netId(0).build();
            packet.getActions().add(new InventoryActionData(InventorySource.fromWorldInteraction(InventorySource.Flag.DROP_ITEM), 0, ItemData.AIR, item));
            packet.getActions().add(new InventoryActionData(InventorySource.fromContainerWindowId(inv.getID(slot)), inv.getSlot(slot), item, ItemData.AIR));
            player.sendPacketNoEvent(packet);
        }

        InventorySlotPacket updatePacket = new InventorySlotPacket();
        updatePacket.setSlot(inv.getSlot(slot));
        updatePacket.setItem(ItemData.AIR);
        updatePacket.setContainerId(inv.getID(slot));
        player.receivePacketNoEvent(updatePacket);
        inv.setItem(slot, ItemData.AIR);
        player.swing();
    }

    public static void moveOrSwap(EntityLocalPlayer player, AbstractInventoryData from, int fromSlot, AbstractInventoryData dest, int destSlot) {
        ItemData fromItem = from.getItem(fromSlot);
        ItemData destItem = dest.getItem(destSlot);
        if (GameDataComponent.inventoriesServerAuthoritative) {
            checkQueueNull();
            int requestID = CreeperBox.getNextRequestID();
            if (destItem == ItemData.AIR) {
                queue.getRequests().add(new ItemStackRequest(requestID, new ItemStackRequestAction[]{new PlaceAction(fromItem.getCount(), new ItemStackRequestSlotData(from.getType(fromSlot), from.getSlot(fromSlot), fromItem.getNetId(),null), new ItemStackRequestSlotData(dest.getType(destSlot), dest.getSlot(destSlot), destItem.getNetId(),null))}, new String[0], (TextProcessingEventOrigin)null));
            } else {
                queue.getRequests().add(new ItemStackRequest(requestID, new ItemStackRequestAction[]{new SwapAction(new ItemStackRequestSlotData(from.getType(fromSlot), from.getSlot(fromSlot), fromItem.getNetId(),null), new ItemStackRequestSlotData(dest.getType(destSlot), dest.getSlot(destSlot), destItem.getNetId(),null))}, new String[0], (TextProcessingEventOrigin)null));
            }
            CreeperBox.setNextRequestID(requestID-2);
        } else {
            InventoryTransactionPacket packet = new InventoryTransactionPacket();
            packet.setTransactionType(InventoryTransactionType.NORMAL);
            packet.getActions().add(new InventoryActionData(InventorySource.fromContainerWindowId(from.getID(fromSlot)), from.getSlot(fromSlot), fromItem, destItem));
            packet.getActions().add(new InventoryActionData(InventorySource.fromContainerWindowId(dest.getID(destSlot)), dest.getSlot(destSlot), destItem, fromItem));
            player.sendPacketNoEvent(packet);
        }

        InventorySlotPacket updatePacket = new InventorySlotPacket();
        updatePacket.setSlot(from.getSlot(fromSlot));
        updatePacket.setItem(destItem);
        updatePacket.setContainerId(from.getID(fromSlot));
        player.receivePacketNoEvent(updatePacket);
        InventorySlotPacket updatePacket2 = new InventorySlotPacket();
        updatePacket2.setSlot(dest.getSlot(destSlot));
        updatePacket2.setItem(fromItem);
        updatePacket2.setContainerId(dest.getID(destSlot));
        player.receivePacketNoEvent(updatePacket2);
        from.setItem(fromSlot, destItem);
        dest.setItem(destSlot, fromItem);
    }

    public static boolean hadBetterItem(ItemData item, ItemDef def, int slot, AbstractInventoryData inv) {
        if (item == ItemData.AIR) {
            return false;
        } else {
            for(String tag : usefulTag) {
                if (def.tags.contains(tag)) {
                    float level = ItemUtil.getLevel(item, def);
                    for(int i = 0; i < inv.getSize(); ++i) {
                        if (i != slot) {
                            ItemData slotData = inv.getItem(i);
                            if (slotData != ItemData.AIR && slotData.getDefinition() instanceof ItemDef) {
                                ItemDef slotDef = (ItemDef)slotData.getDefinition();
                                if (slotDef.tags.contains(tag)) {
                                    float slotLevel = ItemUtil.getLevel(slotData, slotDef);
                                    if (level <= slotLevel) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return false;
        }
    }

    public static List<Integer> findItemSlot(ItemData item, ItemDef data, AbstractInventoryData inv) {
        List<Integer> list = new ArrayList();

        for(int i = 0; i < inv.getSize(); ++i) {
            ItemData slotItem = inv.getItem(i);
            if (slotItem.getDefinition().getIdentifier().equals(item.getDefinition().getIdentifier()) && item.getDamage() == slotItem.getDamage() && (item.getTag() == null || item.getTag().equals(slotItem.getTag())) && (slotItem.getTag() == null || slotItem.getTag().equals(item.getTag()))) {
                list.add(i);
            }
        }

        return list;
    }

    static {
        usefulTag.add("minecraft:is_helmet");
        usefulTag.add("minecraft:is_chestplate");
        usefulTag.add("minecraft:is_leggings");
        usefulTag.add("minecraft:is_boots");
        usefulTag.add("minecraft:is_sword");
        usefulTag.add("minecraft:is_pickaxe");
        usefulTag.add("minecraft:is_axe");
        usefulTag.add("minecraft:is_shovel");
        usefulTag.add("minecraft:is_trident");
        usefulTag.add("minecraft:is_bow");
    }

}

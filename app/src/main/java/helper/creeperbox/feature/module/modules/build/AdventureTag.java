package helper.creeperbox.feature.module.modules.build;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.sdk.InstanceGenerator;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.ItemDef;

@ModuleInfo(name = "冒险标签", category = Category.Build)
public class AdventureTag extends Module {

    private final ListValue mode = new ListValue("模式",this,"破坏")
            .addSubList("破坏")
            .addSubList("放置");

    private static String[] list;
    static {
        List<String> nameList = new ArrayList<>();
        for(ItemDef def : InstanceGenerator.itemMap.map.values()){
            nameList.add(def.name);
        }
        list = nameList.toArray(new String[0]);
    }

    private boolean isFirst;

    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        if(isFirst){
            boolean isBreak = mode.getCurrentValue().equals("破坏");
            isFirst = false;
            ItemData data;
            if(isBreak){
                data = player.getItemInHand().getItemData().toBuilder().canBreak(list).build();
            }else{
                data = player.getItemInHand().getItemData().toBuilder().canPlace(list).build();
            }

            InventorySlotPacket packet = new InventorySlotPacket();
            packet.setContainerId(0);
            packet.setSlot(player.getInventory().getSelected());
            packet.setItem(data);
            player.receivePacketNoEvent(packet);
        }
    }

    @Override
    public void onEnable() {
        isFirst = true;
    }


    @Override
    public String getTag() {
        switch(mode.getCurrentValue()) {
            case "破坏":
                return "Break";
            default:
                return "Place";
        }
    }

}

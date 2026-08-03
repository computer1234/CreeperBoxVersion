package helper.creeperbox.feature.component;

import org.cloudburstmc.protocol.bedrock.packet.BlockEntityDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket;

import java.io.IOException;
import java.util.HashMap;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.module.modules.render.CommandBlockOutput;
import helper.creeperbox.sdk.network.packet.PyRpcPacket;

public class CommandBlockComponent {

    public static HashMap<CommandBlockOutput.BlockPos, CommandBlockOutput.CommandBlockData> commandBlockMap = new HashMap<CommandBlockOutput.BlockPos, CommandBlockOutput.CommandBlockData>();


    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event) {

        if(event.getPacket() instanceof BlockEntityDataPacket){
            BlockEntityDataPacket packet = (BlockEntityDataPacket) event.getPacket();
            if(packet.getData().getString("id").equals("CommandBlock")){
                boolean condition = packet.getData().getBoolean("conditionalMode",false);
                boolean needRedStone = packet.getData().getBoolean("auto",false);
                int tickDelay = packet.getData().getInt("TickDelay",0);
                String command = packet.getData().getString("Command","");
                CommandBlockOutput.CommandBlockData data = new CommandBlockOutput.CommandBlockData(condition,needRedStone,tickDelay,command);
                commandBlockMap.put(new CommandBlockOutput.BlockPos(packet.getBlockPosition().getX(),packet.getBlockPosition().getY(),packet.getBlockPosition().getZ()),data);
            }
        }

        if(event.getPacket() instanceof NetworkSettingsPacket){
            commandBlockMap.clear();
        }
    }


    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if(event.getPacket() instanceof PyRpcPacket) {
            String value = null;
            try {
                value = ((PyRpcPacket) event.getPacket()).generatorUnpacker().unpackValue().toString();
            } catch (IOException e) {
                return;
            }
            if(value.contains("新春快乐,这是我的春节礼物,新的一年开开心心快快乐乐")) {
                event.setCancelled();
            }
        }
    }


}

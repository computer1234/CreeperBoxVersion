package helper.creeperbox.sdk.network.serializer;


import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.bedrock.codec.v419.serializer.ResourcePackStackSerializer_v419;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;

import helper.creeperbox.sdk.network.packet.modify.NeteaseAddEntityPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseContainerOpenPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseResourcePackStackPacket;
import io.netty.buffer.ByteBuf;

public class ResourcePackStackSerializer extends ResourcePackStackSerializer_v419 {


    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, ResourcePackStackPacket packet) {
        super.serialize(buffer, helper, packet);

        NeteaseResourcePackStackPacket newPacket = (NeteaseResourcePackStackPacket) packet;
        buffer.writeBoolean(newPacket.isHasEditorPacks());
        if(newPacket.isHasExtra()){
            helper.writeArray(buffer, newPacket.getNeteaseVipResourcePacks(), this::writeEntry);
            helper.writeArray(buffer, newPacket.getNeteaseVipBehaviorPacks(), this::writeEntry);
        }
    }


    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, ResourcePackStackPacket packet) {
        super.deserialize(buffer, helper, packet);
        NeteaseResourcePackStackPacket newPacket = (NeteaseResourcePackStackPacket) packet;
        newPacket.setHasEditorPacks(buffer.readBoolean());
        newPacket.setHasExtra(buffer.readableBytes() != 0);
        if(newPacket.isHasExtra()) {
            helper.readArray(buffer, newPacket.getNeteaseVipResourcePacks(), this::readEntry);
            helper.readArray(buffer, newPacket.getNeteaseVipBehaviorPacks(), this::readEntry);
        }
    }
}

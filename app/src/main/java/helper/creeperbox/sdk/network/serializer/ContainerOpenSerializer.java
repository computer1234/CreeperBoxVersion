package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.modify.NeteaseContainerOpenPacket;
import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.common.util.VarInts;

public class ContainerOpenSerializer implements BedrockPacketSerializer<NeteaseContainerOpenPacket> {

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseContainerOpenPacket packet) {
        buffer.writeByte(packet.getId());
        buffer.writeByte(packet.getType().getId());
        helper.writeBlockPosition(buffer, packet.getBlockPosition());
        VarInts.writeLong(buffer, packet.getUniqueEntityId());
        if(packet.isHasExtra()) {
            buffer.writeBoolean(packet.isExtra1());
            helper.writeString(buffer,packet.getExtra2());
            helper.writeString(buffer,packet.getExtra3());
            buffer.writeBoolean(packet.isExtra4());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseContainerOpenPacket packet) {
        packet.setId(buffer.readByte());
        packet.setType(ContainerType.from(buffer.readByte()));
        packet.setBlockPosition(helper.readBlockPosition(buffer));
        packet.setUniqueEntityId(VarInts.readLong(buffer));
        packet.setHasExtra(buffer.readableBytes() != 0);
        if(packet.isHasExtra()) {
            packet.setExtra1(buffer.readBoolean());
            packet.setExtra2(helper.readString(buffer));
            packet.setExtra3(helper.readString(buffer));
            packet.setExtra4(buffer.readBoolean());
        }
    }
}

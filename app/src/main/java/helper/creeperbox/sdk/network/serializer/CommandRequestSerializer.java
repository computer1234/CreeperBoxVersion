package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.modify.NeteaseCommandRequestPacket;
import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.common.util.VarInts;

public class CommandRequestSerializer implements BedrockPacketSerializer<NeteaseCommandRequestPacket> {

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseCommandRequestPacket packet) {

        helper.writeString(buffer, packet.getCommand());
        helper.writeCommandOrigin(buffer, packet.getCommandOriginData());
        buffer.writeBoolean(packet.isInternal());
        VarInts.writeInt(buffer, packet.getVersion());
        if(packet.isHasExtra()) {
            buffer.writeBoolean(packet.isExtra1());
        }
      }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseCommandRequestPacket packet) {
        packet.setCommand(helper.readString(buffer));
        packet.setCommandOriginData(helper.readCommandOrigin(buffer));
        packet.setInternal(buffer.readBoolean());
        packet.setVersion(VarInts.readInt(buffer));
        packet.setHasExtra(buffer.readableBytes() != 0);
        if(packet.isHasExtra()) {
            packet.setExtra1(buffer.readBoolean());
        }
    }
}

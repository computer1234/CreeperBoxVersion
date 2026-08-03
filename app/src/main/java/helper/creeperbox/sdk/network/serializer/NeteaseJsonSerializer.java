package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.NeteaseJsonPacket;
import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;

public class NeteaseJsonSerializer implements BedrockPacketSerializer<NeteaseJsonPacket> {
    public static final NeteaseJsonSerializer INSTANCE = new NeteaseJsonSerializer();

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseJsonPacket packet) {
        helper.writeString(buffer,packet.getJson());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseJsonPacket packet) {
        packet.setJson(helper.readString(buffer));
    }

}

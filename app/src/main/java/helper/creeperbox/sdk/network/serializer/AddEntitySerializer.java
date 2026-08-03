package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.modify.NeteaseAddEntityPacket;
import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v557.serializer.AddEntitySerializer_v557;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;

public class AddEntitySerializer extends AddEntitySerializer_v557 {


    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, AddEntityPacket packet) {
        super.serialize(buffer, helper, packet);
        NeteaseAddEntityPacket newPacket = (NeteaseAddEntityPacket) packet;
        if(newPacket.isHasExtra()){
            helper.writeString(buffer,newPacket.getExtra1());
            helper.writeString(buffer,newPacket.getExtra2());
            helper.writeString(buffer,newPacket.getExtra3());
            buffer.writeBoolean(newPacket.isExtra4());
            buffer.writeBoolean(newPacket.isExtra5());
            buffer.writeBoolean(newPacket.isExtra6());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, AddEntityPacket packet) {
        super.deserialize(buffer,helper,packet);
        NeteaseAddEntityPacket newPacket = (NeteaseAddEntityPacket) packet;
        newPacket.setHasExtra(buffer.readableBytes() != 0);
        if(newPacket.isHasExtra()) {
            newPacket.setExtra1(helper.readString(buffer));
            newPacket.setExtra2(helper.readString(buffer));
            newPacket.setExtra3(helper.readString(buffer));
            newPacket.setExtra4(buffer.readBoolean());
            newPacket.setExtra5(buffer.readBoolean());
            newPacket.setExtra6(buffer.readBoolean());
        }
    }


}

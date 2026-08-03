package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;
import  io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.bedrock.codec.v554.serializer.TextSerializer_v554;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

public class NeteaseTextSerializer implements BedrockPacketSerializer<NeteaseTextPacket> {

    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseTextPacket packet) {

        NeteaseTextPacket.Type type = TextPacket.Type.values()[buffer.readUnsignedByte()];
        packet.setType(type);
        packet.setNeedsTranslation(buffer.readBoolean());
        switch (type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                packet.setSourceName(helper.readString(buffer));
            case RAW:
            case TIP:
            case SYSTEM:
            case JSON:
            case WHISPER_JSON:
            case ANNOUNCEMENT_JSON:
                packet.setMessage(helper.readString(buffer));
                break;
            case TRANSLATION:
            case POPUP:
            case JUKEBOX_POPUP:
                packet.setMessage(helper.readString(buffer));
                helper.readArray(buffer, packet.getParameters(), helper::readString);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported TextType " + type);
        }

        packet.setXuid(helper.readString(buffer));
        packet.setPlatformChatId(helper.readString(buffer));
        packet.setFilteredMessage(helper.readString(buffer));

        packet.setHasExtra(buffer.readableBytes() != 0);
        if(packet.isHasExtra()){
            if(packet.getType() == TextPacket.Type.CHAT){
                helper.readArray(buffer, packet.getParameters(), helper::readString);
            }else if(packet.getType() == TextPacket.Type.POPUP){
                packet.setAuthor(helper.readString(buffer));
            }
        }

    }

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseTextPacket packet) {

        NeteaseTextPacket.Type type = packet.getType();
        buffer.writeByte(type.ordinal());
        buffer.writeBoolean(packet.isNeedsTranslation());
        switch (type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                helper.writeString(buffer, packet.getSourceName());
            case RAW:
            case TIP:
            case SYSTEM:
            case JSON:
            case WHISPER_JSON:
            case ANNOUNCEMENT_JSON:
                helper.writeString(buffer, packet.getMessage());
                break;
            case TRANSLATION:
            case POPUP:
            case JUKEBOX_POPUP:
                helper.writeString(buffer, packet.getMessage());
                helper.writeArray(buffer, packet.getParameters(), helper::writeString);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported TextType " + type);
        }

        helper.writeString(buffer, packet.getXuid());
        helper.writeString(buffer, packet.getPlatformChatId());
        helper.writeString(buffer,packet.getFilteredMessage());

        if(packet.isHasExtra()){
            if(packet.getType() == TextPacket.Type.CHAT){
                helper.writeArray(buffer, packet.getParameters(), helper::writeString);
            }else if(packet.getType() == TextPacket.Type.POPUP){
                helper.writeString(buffer,packet.getAuthor());
            }
        }

    }


}

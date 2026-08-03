package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.ConfirmSkinPacket;
import io.netty.buffer.ByteBuf;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.util.ArrayList;
import java.util.List;

public class ConfirmSkinSerializer implements BedrockPacketSerializer<ConfirmSkinPacket> {

    public static final ConfirmSkinSerializer INSTANCE = new ConfirmSkinSerializer();

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, ConfirmSkinPacket packet) {
        VarInts.writeUnsignedInt(buffer, packet.getEntries().size());
        for (final ConfirmSkinPacket.SkinEntry entry : packet.getEntries()) {
            buffer.writeBoolean(entry.isValid());
            helper.writeUuid(buffer, entry.getUuid());
            helper.writeByteArray(buffer, entry.getSkinBytes());
        }
        for (final ConfirmSkinPacket.SkinEntry entry : packet.getEntries()) {
            helper.writeString(buffer, entry.getUidStr());
        }
        for (final ConfirmSkinPacket.SkinEntry entry : packet.getEntries()) {
            helper.writeString(buffer, entry.getGeoStr());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, ConfirmSkinPacket packet) {

        final int size = VarInts.readUnsignedInt(buffer);
        final List<ConfirmSkinPacket.SkinEntry> entries = new ArrayList<ConfirmSkinPacket.SkinEntry>();
        for (int i = 0; i < size; ++i) {
            final ConfirmSkinPacket.SkinEntry entry = new ConfirmSkinPacket.SkinEntry();
            entry.setValid(buffer.readBoolean());
            entry.setUuid(helper.readUuid(buffer));
            entry.setSkinBytes(helper.readByteArray(buffer));
            entries.add(entry);
        }

        if (buffer.isReadable()) {
            for (int i = 0; i < size; ++i) {
                entries.get(i).setUidStr(helper.readString(buffer));
            }
        }
        if (buffer.isReadable()) {
            for (int i = 0; i < size; ++i) {
                entries.get(i).setGeoStr(helper.readString(buffer));
            }
        }

        packet.setEntries(entries);
    }
}

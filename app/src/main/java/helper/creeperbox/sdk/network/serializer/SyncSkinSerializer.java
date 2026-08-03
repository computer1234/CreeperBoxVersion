package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.SyncSkinPacket;
import io.netty.buffer.ByteBuf;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.util.ArrayList;
import java.util.List;

public class SyncSkinSerializer implements BedrockPacketSerializer<SyncSkinPacket> {

    public static final SyncSkinSerializer INSTANCE = new SyncSkinSerializer();

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, SyncSkinPacket packet) {
        VarInts.writeUnsignedInt(buffer, packet.getEntries().size());
        // 轮 1: bool + UUID + skinBytes
        for (final SyncSkinPacket.SkinEntry entry : packet.getEntries()) {
            buffer.writeBoolean(entry.isValid());
            helper.writeUuid(buffer, entry.getUuid());
            helper.writeByteArray(buffer, entry.getSkinBytes());
        }
        // 轮 2: udid
        for (final SyncSkinPacket.SkinEntry entry : packet.getEntries()) {
            helper.writeString(buffer, entry.getUdid());
        }
        // 轮 3: extraData
        for (final SyncSkinPacket.SkinEntry entry : packet.getEntries()) {
            helper.writeString(buffer, entry.getExtraData());
        }
        // 轮 4: itemId
        for (final SyncSkinPacket.SkinEntry entry : packet.getEntries()) {
            helper.writeString(buffer, entry.getItemId());
        }
        // SerializedSkin
        if (packet.getSkin() != null) {
            helper.writeSkin(buffer, packet.getSkin());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, SyncSkinPacket packet) {

        final int size = VarInts.readUnsignedInt(buffer);
        final List<SyncSkinPacket.SkinEntry> entries = new ArrayList<SyncSkinPacket.SkinEntry>();
        for (int i = 0; i < size; ++i) {
            final SyncSkinPacket.SkinEntry entry = new SyncSkinPacket.SkinEntry();
            entry.setValid(buffer.readBoolean());
            entry.setUuid(helper.readUuid(buffer));
            entry.setSkinBytes(helper.readByteArray(buffer));
            entries.add(entry);
        }

        if (buffer.isReadable()) {
            for (int i = 0; i < size; ++i) {
                entries.get(i).setUdid(helper.readString(buffer));
            }
        }
        if (buffer.isReadable()) {
            for (int i = 0; i < size; ++i) {
                entries.get(i).setExtraData(helper.readString(buffer));
            }
        }
        if (buffer.isReadable()) {
            for (int i = 0; i < size; ++i) {
                entries.get(i).setItemId(helper.readString(buffer));
            }
        }

        packet.setEntries(entries);

        // SerializedSkin
        if (buffer.isReadable()) {
            packet.setSkin(helper.readSkin(buffer));
        }
    }
}

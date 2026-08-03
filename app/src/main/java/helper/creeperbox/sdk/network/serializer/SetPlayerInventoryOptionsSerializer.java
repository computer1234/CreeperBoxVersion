package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.modify.NeteaseSetPlayerInventoryOptionsPacket;
import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.bedrock.data.inventory.InventoryLayout;
import org.cloudburstmc.protocol.bedrock.data.inventory.InventoryTabLeft;
import org.cloudburstmc.protocol.bedrock.data.inventory.InventoryTabRight;
import org.cloudburstmc.protocol.common.util.VarInts;

public class SetPlayerInventoryOptionsSerializer implements BedrockPacketSerializer<NeteaseSetPlayerInventoryOptionsPacket> {

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseSetPlayerInventoryOptionsPacket packet) {
        VarInts.writeInt(buffer, packet.getLeftTab().ordinal());
        VarInts.writeInt(buffer, packet.getRightTab().ordinal());
        buffer.writeBoolean(packet.isFiltering());
        VarInts.writeInt(buffer, packet.getLayout().ordinal());
        VarInts.writeInt(buffer, packet.getCraftingLayout().ordinal());
        if (packet.isHasExtra()) {
            VarInts.writeInt(buffer, packet.getExtra());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseSetPlayerInventoryOptionsPacket packet) {
        packet.setLeftTab(InventoryTabLeft.VALUES[VarInts.readInt(buffer)]);
        packet.setRightTab(InventoryTabRight.VALUES[VarInts.readInt(buffer)]);
        packet.setFiltering(buffer.readBoolean());
        packet.setLayout(InventoryLayout.VALUES[VarInts.readInt(buffer)]);
        packet.setCraftingLayout(InventoryLayout.VALUES[VarInts.readInt(buffer)]);
        packet.setHasExtra(buffer.readableBytes() != 0);
        if (packet.isHasExtra()) {
            packet.setExtra(VarInts.readInt(buffer));
        }
    }
}

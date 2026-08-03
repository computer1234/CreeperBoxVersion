package helper.creeperbox.sdk.network.serializer;

import helper.creeperbox.sdk.network.packet.modify.NeteaseAnimatePacket;
import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.common.util.Int2ObjectBiMap;
import org.cloudburstmc.protocol.common.util.VarInts;

public class AnimateSerializer implements BedrockPacketSerializer<NeteaseAnimatePacket>{


    private static final Int2ObjectBiMap<NeteaseAnimatePacket.Action> types = new Int2ObjectBiMap();

    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseAnimatePacket packet) {
        NeteaseAnimatePacket.Action action = packet.getAction();
        VarInts.writeInt(buffer, types.get(action));
        VarInts.writeUnsignedLong(buffer, packet.getRuntimeEntityId());
        if (action == NeteaseAnimatePacket.Action.ROW_LEFT || action == NeteaseAnimatePacket.Action.ROW_RIGHT) {
            buffer.writeFloatLE(packet.getRowingTime());
        }
        if (packet.getAction() == NeteaseAnimatePacket.Action.CRITICAL_HIT && packet.isHasExtra()) {
            VarInts.writeUnsignedLong(buffer,packet.getExtraCriticalEntityId());
        }
    }

    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseAnimatePacket packet) {
        NeteaseAnimatePacket.Action action = (NeteaseAnimatePacket.Action)types.get(VarInts.readInt(buffer));
        packet.setAction(action);
        packet.setRuntimeEntityId(VarInts.readUnsignedLong(buffer));
        if (action == NeteaseAnimatePacket.Action.ROW_LEFT || action == NeteaseAnimatePacket.Action.ROW_RIGHT) {
            packet.setRowingTime(buffer.readFloatLE());
        }
        if (packet.getAction() == NeteaseAnimatePacket.Action.CRITICAL_HIT) {
            packet.setHasExtra(buffer.readableBytes() != 0);
            if(packet.isHasExtra()) {
                packet.setExtraCriticalEntityId(VarInts.readUnsignedLong(buffer));
            }
        }
    }

    public AnimateSerializer() {
    }

    static {
        types.put(0, NeteaseAnimatePacket.Action.NO_ACTION);
        types.put(1, NeteaseAnimatePacket.Action.SWING_ARM);
        types.put(3, NeteaseAnimatePacket.Action.WAKE_UP);
        types.put(4, NeteaseAnimatePacket.Action.CRITICAL_HIT);
        types.put(5, NeteaseAnimatePacket.Action.MAGIC_CRITICAL_HIT);
        types.put(128, NeteaseAnimatePacket.Action.ROW_RIGHT);
        types.put(129, NeteaseAnimatePacket.Action.ROW_LEFT);
    }
}

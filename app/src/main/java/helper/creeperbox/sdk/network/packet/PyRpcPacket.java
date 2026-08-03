package helper.creeperbox.sdk.network.packet;

import java.util.Arrays;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

public class PyRpcPacket implements BedrockPacket {
    private byte[] data;
    private long msgId;

    public PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.UNKNOWN;
    }

    @Override
    public BedrockPacket clone() {
        try {
            return (PyRpcPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public MessageUnpacker generatorUnpacker() {
        return MessagePack.newDefaultUnpacker(this.data);
    }

    public String toString() {
        String var10000 = Arrays.toString(this.data);
        return "PyRpcPacket(data=" + var10000 + ", msgId=" + this.msgId + ")";
    }

    public PyRpcPacket() {
    }

    public byte[] getData() {
        return this.data;
    }

    public long getMsgId() {
        return this.msgId;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PyRpcPacket)) {
            return false;
        } else {
            PyRpcPacket other = (PyRpcPacket)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.msgId != other.msgId) {
                return false;
            } else {
                return Arrays.equals(this.data, other.data);
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PyRpcPacket;
    }

    public int hashCode() {
        int result = 1;
        long $msgId = this.msgId;
        result = result * 59 + (int)($msgId >>> 32 ^ $msgId);
        result = result * 59 + Arrays.hashCode(this.data);
        return result;
    }
}

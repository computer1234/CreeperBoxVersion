package helper.creeperbox.sdk.network.packet;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;

public class NeteaseJsonPacket implements BedrockPacket {

    @Override
    public NeteaseJsonPacket clone() {
        try {
            return (NeteaseJsonPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private String json;

    public PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.UNKNOWN;
    }

    public String toString() {
        return "NeteaseJsonPacket(json=" + this.json + ")";
    }

    public NeteaseJsonPacket() {
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseJsonPacket)) {
            return false;
        } else {
            NeteaseJsonPacket other = (NeteaseJsonPacket)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$json = this.json;
                Object other$json = other.json;
                if (this$json == null) {
                    if (other$json != null) {
                        return false;
                    }
                } else if (!this$json.equals(other$json)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteaseJsonPacket;
    }

    public int hashCode() {
        int result = 1;
        Object $json = this.json;
        result = result * 59 + ($json == null ? 43 : $json.hashCode());
        return result;
    }
}

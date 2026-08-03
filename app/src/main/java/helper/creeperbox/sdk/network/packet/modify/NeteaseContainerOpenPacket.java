package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;

public class NeteaseContainerOpenPacket implements BedrockPacket {

    @Override
    public NeteaseContainerOpenPacket clone() {
        try {
            return (NeteaseContainerOpenPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private byte id;
    private ContainerType type;
    private Vector3i blockPosition;
    private long uniqueEntityId = -1L;
    private boolean hasExtra;
    private boolean extra1;
    private String extra2;
    private String extra3;
    private boolean extra4;

    public final PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.CONTAINER_OPEN;
    }

    public NeteaseContainerOpenPacket() {
    }

    public byte getId() {
        return this.id;
    }

    public ContainerType getType() {
        return this.type;
    }

    public Vector3i getBlockPosition() {
        return this.blockPosition;
    }

    public long getUniqueEntityId() {
        return this.uniqueEntityId;
    }

    public boolean isHasExtra() {
        return this.hasExtra;
    }

    public boolean isExtra1() {
        return this.extra1;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public void setType(ContainerType type) {
        this.type = type;
    }

    public void setBlockPosition(Vector3i blockPosition) {
        this.blockPosition = blockPosition;
    }

    public void setUniqueEntityId(long uniqueEntityId) {
        this.uniqueEntityId = uniqueEntityId;
    }

    public void setHasExtra(boolean hasExtra) {
        this.hasExtra = hasExtra;
    }

    public void setExtra1(boolean extra1) {
        this.extra1 = extra1;
    }


    public String getExtra2() {
        return extra2;
    }

    public String getExtra3() {
        return extra3;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }

    public void setExtra4(boolean extra4) {
        this.extra4 = extra4;
    }

    public boolean isExtra4() {
        return extra4;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseContainerOpenPacket)) {
            return false;
        } else {
            NeteaseContainerOpenPacket other = (NeteaseContainerOpenPacket) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.id != other.id) {
                return false;
            } else if (this.uniqueEntityId != other.uniqueEntityId) {
                return false;
            } else if (this.hasExtra != other.hasExtra) {
                return false;
            } else if (this.extra1 != other.extra1) {
                return false;
            } else if (this.extra4 != other.extra4) {
                return false;
            } else {
                Object this$type = this.type;
                Object other$type = other.type;
                if (this$type == null) {
                    if (other$type != null) {
                        return false;
                    }
                } else if (!this$type.equals(other$type)) {
                    return false;
                }

                Object this$blockPosition = this.blockPosition;
                Object other$blockPosition = other.blockPosition;
                if (this$blockPosition == null) {
                    if (other$blockPosition != null) {
                        return false;
                    }
                } else if (!this$blockPosition.equals(other$blockPosition)) {
                    return false;
                }

                Object this$extra2 = this.extra2;
                Object other$extra2 = other.extra2;
                if (this$extra2 == null) {
                    if (other$extra2 != null) {
                        return false;
                    }
                } else if (!this$extra2.equals(other$extra2)) {
                    return false;
                }

                Object this$extra3 = this.extra3;
                Object other$extra3 = other.extra3;
                if (this$extra3 == null) {
                    if (other$extra3 != null) {
                        return false;
                    }
                } else if (!this$extra3.equals(other$extra3)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteaseContainerOpenPacket;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.id;
        long $uniqueEntityId = this.uniqueEntityId;
        result = result * 59 + (int) ($uniqueEntityId >>> 32 ^ $uniqueEntityId);
        result = result * 59 + (this.hasExtra ? 79 : 97);
        result = result * 59 + (this.extra1 ? 79 : 97);
        result = result * 59 + (this.extra4 ? 79 : 97);
        Object $type = this.type;
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $blockPosition = this.blockPosition;
        result = result * 59 + ($blockPosition == null ? 43 : $blockPosition.hashCode());
        Object $extra2 = this.extra2;
        result = result * 59 + ($extra2 == null ? 43 : $extra2.hashCode());
        Object $extra3 = this.extra3;
        result = result * 59 + ($extra3 == null ? 43 : $extra3.hashCode());
        return result;
    }

    public String toString() {
        return "NeteaseContainerOpenPacket(id=" + this.id + ", type=" + this.type + ", blockPosition=" + this.blockPosition + ", uniqueEntityId=" + this.uniqueEntityId + ", hasExtra=" + this.hasExtra + ", extra1=" + this.extra1 + ", extra2=" + this.extra2 + ", extra3=" + this.extra3 + ", extra4=" + this.extra4 + ")";
    }
}

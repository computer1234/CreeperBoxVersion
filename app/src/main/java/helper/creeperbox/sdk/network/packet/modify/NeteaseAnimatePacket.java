package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;

public class NeteaseAnimatePacket implements BedrockPacket {

    @Override
    public NeteaseAnimatePacket clone() {
        try {
            return (NeteaseAnimatePacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private float rowingTime;
    private Action action;
    private long runtimeEntityId;
    private boolean hasExtra;
    private long extraCriticalEntityId;

    public final PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.ANIMATE;
    }

    public NeteaseAnimatePacket() {
    }

    public float getRowingTime() {
        return this.rowingTime;
    }

    public Action getAction() {
        return this.action;
    }

    public long getRuntimeEntityId() {
        return this.runtimeEntityId;
    }

    public boolean isHasExtra() {
        return this.hasExtra;
    }

    public long getExtraCriticalEntityId() {
        return this.extraCriticalEntityId;
    }

    public void setRowingTime(float rowingTime) {
        this.rowingTime = rowingTime;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setRuntimeEntityId(long runtimeEntityId) {
        this.runtimeEntityId = runtimeEntityId;
    }

    public void setHasExtra(boolean hasExtra) {
        this.hasExtra = hasExtra;
    }

    public void setExtraCriticalEntityId(long extraCriticalEntityId) {
        this.extraCriticalEntityId = extraCriticalEntityId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseAnimatePacket)) {
            return false;
        } else {
            NeteaseAnimatePacket other = (NeteaseAnimatePacket)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (Float.compare(this.rowingTime, other.rowingTime) != 0) {
                return false;
            } else if (this.runtimeEntityId != other.runtimeEntityId) {
                return false;
            } else if (this.hasExtra != other.hasExtra) {
                return false;
            } else if (this.extraCriticalEntityId != other.extraCriticalEntityId) {
                return false;
            } else {
                Object this$action = this.action;
                Object other$action = other.action;
                if (this$action == null) {
                    if (other$action != null) {
                        return false;
                    }
                } else if (!this$action.equals(other$action)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteaseAnimatePacket;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.rowingTime);
        long $runtimeEntityId = this.runtimeEntityId;
        result = result * 59 + (int)($runtimeEntityId >>> 32 ^ $runtimeEntityId);
        result = result * 59 + (this.hasExtra ? 79 : 97);
        long $extraCriticalEntityId = this.extraCriticalEntityId;
        result = result * 59 + (int)($extraCriticalEntityId >>> 32 ^ $extraCriticalEntityId);
        Object $action = this.action;
        result = result * 59 + ($action == null ? 43 : $action.hashCode());
        return result;
    }

    public String toString() {
        return "NeteaseAnimatePacket(rowingTime=" + this.rowingTime + ", action=" + this.action + ", runtimeEntityId=" + this.runtimeEntityId + ", hasExtra=" + this.hasExtra + ", extraCriticalEntityId=" + this.extraCriticalEntityId + ")";
    }

    public static enum Action {
        NO_ACTION,
        SWING_ARM,
        WAKE_UP,
        CRITICAL_HIT,
        MAGIC_CRITICAL_HIT,
        ROW_RIGHT,
        ROW_LEFT;

        private Action() {
        }
    }
}

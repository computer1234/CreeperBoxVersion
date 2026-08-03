package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.protocol.bedrock.data.camera.CameraSetInstruction;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

public class NeteaseCameraInstructionPacket implements BedrockPacket {

    @Override
    public NeteaseCameraInstructionPacket clone() {
        try {
            return (NeteaseCameraInstructionPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private CameraSetInstruction setInstruction;
    private NeteaseCameraFadeInstruction fadeInstruction;
    private OptionalBoolean clear = OptionalBoolean.empty();

    public PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.CAMERA_INSTRUCTION;
    }

    public void setClear(boolean value) {
        this.clear = OptionalBoolean.of(value);
    }

    public void setClear(OptionalBoolean clear) {
        this.clear = clear;
    }

    public NeteaseCameraInstructionPacket() {
    }

    public CameraSetInstruction getSetInstruction() {
        return this.setInstruction;
    }

    public NeteaseCameraFadeInstruction getFadeInstruction() {
        return this.fadeInstruction;
    }

    public OptionalBoolean getClear() {
        return this.clear;
    }

    public void setSetInstruction(CameraSetInstruction setInstruction) {
        this.setInstruction = setInstruction;
    }

    public void setFadeInstruction(NeteaseCameraFadeInstruction fadeInstruction) {
        this.fadeInstruction = fadeInstruction;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseCameraInstructionPacket)) {
            return false;
        } else {
            NeteaseCameraInstructionPacket other = (NeteaseCameraInstructionPacket)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$setInstruction = this.setInstruction;
                    Object other$setInstruction = other.setInstruction;
                    if (this$setInstruction == null) {
                        if (other$setInstruction == null) {
                            break label47;
                        }
                    } else if (this$setInstruction.equals(other$setInstruction)) {
                        break label47;
                    }

                    return false;
                }

                Object this$fadeInstruction = this.fadeInstruction;
                Object other$fadeInstruction = other.fadeInstruction;
                if (this$fadeInstruction == null) {
                    if (other$fadeInstruction != null) {
                        return false;
                    }
                } else if (!this$fadeInstruction.equals(other$fadeInstruction)) {
                    return false;
                }

                Object this$clear = this.clear;
                Object other$clear = other.clear;
                if (this$clear == null) {
                    if (other$clear != null) {
                        return false;
                    }
                } else if (!this$clear.equals(other$clear)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteaseCameraInstructionPacket;
    }

    public int hashCode() {
        int result = 1;
        Object $setInstruction = this.setInstruction;
        result = result * 59 + ($setInstruction == null ? 43 : $setInstruction.hashCode());
        Object $fadeInstruction = this.fadeInstruction;
        result = result * 59 + ($fadeInstruction == null ? 43 : $fadeInstruction.hashCode());
        Object $clear = this.clear;
        result = result * 59 + ($clear == null ? 43 : $clear.hashCode());
        return result;
    }

    public String toString() {
        return "NeteaseCameraInstructionPacket(setInstruction=" + this.setInstruction + ", fadeInstruction=" + this.fadeInstruction + ", clear=" + this.clear + ")";
    }

}

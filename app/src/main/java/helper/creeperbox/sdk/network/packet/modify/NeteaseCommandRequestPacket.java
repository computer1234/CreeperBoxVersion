package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;

public class NeteaseCommandRequestPacket implements BedrockPacket {

    @Override
    public NeteaseCommandRequestPacket clone() {
        try {
            return (NeteaseCommandRequestPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private String command;
    private CommandOriginData commandOriginData;
    private boolean internal;
    private int version;
    private boolean hasExtra;
    private boolean extra1;

    public final PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.COMMAND_REQUEST;
    }

    public NeteaseCommandRequestPacket() {
    }

    public String getCommand() {
        return this.command;
    }

    public CommandOriginData getCommandOriginData() {
        return this.commandOriginData;
    }

    public boolean isInternal() {
        return this.internal;
    }

    public int getVersion() {
        return this.version;
    }

    public boolean isHasExtra() {
        return this.hasExtra;
    }

    public boolean isExtra1() {
        return this.extra1;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setCommandOriginData(CommandOriginData commandOriginData) {
        this.commandOriginData = commandOriginData;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setHasExtra(boolean hasExtra) {
        this.hasExtra = hasExtra;
    }

    public void setExtra1(boolean extra1) {
        this.extra1 = extra1;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseCommandRequestPacket)) {
            return false;
        } else {
            NeteaseCommandRequestPacket other = (NeteaseCommandRequestPacket)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.internal != other.internal) {
                return false;
            } else if (this.version != other.version) {
                return false;
            } else if (this.hasExtra != other.hasExtra) {
                return false;
            } else if (this.extra1 != other.extra1) {
                return false;
            } else {
                Object this$command = this.command;
                Object other$command = other.command;
                if (this$command == null) {
                    if (other$command != null) {
                        return false;
                    }
                } else if (!this$command.equals(other$command)) {
                    return false;
                }

                Object this$commandOriginData = this.commandOriginData;
                Object other$commandOriginData = other.commandOriginData;
                if (this$commandOriginData == null) {
                    if (other$commandOriginData != null) {
                        return false;
                    }
                } else if (!this$commandOriginData.equals(other$commandOriginData)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteaseCommandRequestPacket;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + (this.internal ? 79 : 97);
        result = result * 59 + this.version;
        result = result * 59 + (this.hasExtra ? 79 : 97);
        result = result * 59 + (this.extra1 ? 79 : 97);
        Object $command = this.command;
        result = result * 59 + ($command == null ? 43 : $command.hashCode());
        Object $commandOriginData = this.commandOriginData;
        result = result * 59 + ($commandOriginData == null ? 43 : $commandOriginData.hashCode());
        return result;
    }

    public String toString() {
        return "NeteaseCommandRequestPacket(command=" + this.command + ", commandOriginData=" + this.commandOriginData + ", internal=" + this.internal + ", version=" + this.version + ", hasExtra=" + this.hasExtra + ", extra1=" + this.extra1 + ")";
    }
}

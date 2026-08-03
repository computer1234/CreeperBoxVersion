package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.protocol.bedrock.data.inventory.InventoryLayout;
import org.cloudburstmc.protocol.bedrock.data.inventory.InventoryTabLeft;
import org.cloudburstmc.protocol.bedrock.data.inventory.InventoryTabRight;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;

public class NeteaseSetPlayerInventoryOptionsPacket implements BedrockPacket {

    @Override
    public NeteaseSetPlayerInventoryOptionsPacket clone() {
        try {
            return (NeteaseSetPlayerInventoryOptionsPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private InventoryTabLeft leftTab;
    private InventoryTabRight rightTab;
    private boolean filtering;
    private InventoryLayout layout;
    private InventoryLayout craftingLayout;
    private boolean hasExtra;
    private int extra;

    public final PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.SET_PLAYER_INVENTORY_OPTIONS;
    }

    public NeteaseSetPlayerInventoryOptionsPacket() {
    }

    public InventoryTabLeft getLeftTab() {
        return this.leftTab;
    }

    public InventoryTabRight getRightTab() {
        return this.rightTab;
    }

    public boolean isFiltering() {
        return this.filtering;
    }

    public InventoryLayout getLayout() {
        return this.layout;
    }

    public InventoryLayout getCraftingLayout() {
        return this.craftingLayout;
    }

    public boolean isHasExtra() {
        return this.hasExtra;
    }

    public int getExtra() {
        return this.extra;
    }

    public void setLeftTab(InventoryTabLeft leftTab) {
        this.leftTab = leftTab;
    }

    public void setRightTab(InventoryTabRight rightTab) {
        this.rightTab = rightTab;
    }

    public void setFiltering(boolean filtering) {
        this.filtering = filtering;
    }

    public void setLayout(InventoryLayout layout) {
        this.layout = layout;
    }

    public void setCraftingLayout(InventoryLayout craftingLayout) {
        this.craftingLayout = craftingLayout;
    }

    public void setHasExtra(boolean hasExtra) {
        this.hasExtra = hasExtra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseSetPlayerInventoryOptionsPacket)) {
            return false;
        } else {
            NeteaseSetPlayerInventoryOptionsPacket other = (NeteaseSetPlayerInventoryOptionsPacket) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.filtering != other.filtering) {
                return false;
            } else if (this.hasExtra != other.hasExtra) {
                return false;
            } else if (this.extra != other.extra) {
                return false;
            } else {
                Object this$leftTab = this.leftTab;
                Object other$leftTab = other.leftTab;
                if (this$leftTab == null) {
                    if (other$leftTab != null) {
                        return false;
                    }
                } else if (!this$leftTab.equals(other$leftTab)) {
                    return false;
                }

                Object this$rightTab = this.rightTab;
                Object other$rightTab = other.rightTab;
                if (this$rightTab == null) {
                    if (other$rightTab != null) {
                        return false;
                    }
                } else if (!this$rightTab.equals(other$rightTab)) {
                    return false;
                }

                Object this$layout = this.layout;
                Object other$layout = other.layout;
                if (this$layout == null) {
                    if (other$layout != null) {
                        return false;
                    }
                } else if (!this$layout.equals(other$layout)) {
                    return false;
                }

                Object this$craftingLayout = this.craftingLayout;
                Object other$craftingLayout = other.craftingLayout;
                if (this$craftingLayout == null) {
                    if (other$craftingLayout != null) {
                        return false;
                    }
                } else if (!this$craftingLayout.equals(other$craftingLayout)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteaseSetPlayerInventoryOptionsPacket;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + (this.filtering ? 79 : 97);
        result = result * 59 + (this.hasExtra ? 79 : 97);
        result = result * 59 + this.extra;
        Object $leftTab = this.leftTab;
        result = result * 59 + ($leftTab == null ? 43 : $leftTab.hashCode());
        Object $rightTab = this.rightTab;
        result = result * 59 + ($rightTab == null ? 43 : $rightTab.hashCode());
        Object $layout = this.layout;
        result = result * 59 + ($layout == null ? 43 : $layout.hashCode());
        Object $craftingLayout = this.craftingLayout;
        result = result * 59 + ($craftingLayout == null ? 43 : $craftingLayout.hashCode());
        return result;
    }

    public String toString() {
        return "NeteaseSetPlayerInventoryOptionsPacket(leftTab=" + this.leftTab + ", rightTab=" + this.rightTab + ", filtering=" + this.filtering + ", layout=" + this.layout + ", craftingLayout=" + this.craftingLayout + ", hasExtra=" + this.hasExtra + ", extra=" + this.extra + ")";
    }
}

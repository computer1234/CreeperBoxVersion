package helper.creeperbox.sdk.network.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;

public class ConfirmSkinPacket implements BedrockPacket {

    @Override
    public ConfirmSkinPacket clone() {
        try {
            return (ConfirmSkinPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private List<SkinEntry> entries = new ArrayList();

    public PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.UNKNOWN;
    }

    public String toString() {
        return "ConfirmSkinPacket(entries=" + this.entries + ")";
    }

    public ConfirmSkinPacket() {
    }

    public List<SkinEntry> getEntries() {
        return this.entries;
    }

    public void setEntries(List<SkinEntry> entries) {
        this.entries = entries;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ConfirmSkinPacket)) {
            return false;
        } else {
            ConfirmSkinPacket other = (ConfirmSkinPacket)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$entries = this.entries;
                Object other$entries = other.entries;
                if (this$entries == null) {
                    if (other$entries != null) {
                        return false;
                    }
                } else if (!this$entries.equals(other$entries)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfirmSkinPacket;
    }

    public int hashCode() {
        int result = 1;
        Object $entries = this.entries;
        result = result * 59 + ($entries == null ? 43 : $entries.hashCode());
        return result;
    }

    public static class SkinEntry {
        private boolean valid;
        private UUID uuid;
        private byte[] skinBytes;
        private String uidStr;
        private String geoStr;

        public String toString() {
            boolean var10000 = this.valid;
            return "ConfirmSkinPacket.SkinEntry(valid=" + var10000 + ", uuid=" + this.uuid + ", skinBytes=" + Arrays.toString(this.skinBytes) + ", uidStr=" + this.uidStr + ", geoStr=" + this.geoStr + ")";
        }

        public SkinEntry() {
        }

        public boolean isValid() {
            return this.valid;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public byte[] getSkinBytes() {
            return this.skinBytes;
        }

        public String getUidStr() {
            return this.uidStr;
        }

        public String getGeoStr() {
            return this.geoStr;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        public void setSkinBytes(byte[] skinBytes) {
            this.skinBytes = skinBytes;
        }

        public void setUidStr(String uidStr) {
            this.uidStr = uidStr;
        }

        public void setGeoStr(String geoStr) {
            this.geoStr = geoStr;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SkinEntry)) {
                return false;
            } else {
                SkinEntry other = (SkinEntry)o;
                if (!other.canEqual(this)) {
                    return false;
                } else if (this.valid != other.valid) {
                    return false;
                } else {
                    Object this$uuid = this.uuid;
                    Object other$uuid = other.uuid;
                    if (this$uuid == null) {
                        if (other$uuid != null) {
                            return false;
                        }
                    } else if (!this$uuid.equals(other$uuid)) {
                        return false;
                    }

                    if (!Arrays.equals(this.skinBytes, other.skinBytes)) {
                        return false;
                    } else {
                        Object this$uidStr = this.uidStr;
                        Object other$uidStr = other.uidStr;
                        if (this$uidStr == null) {
                            if (other$uidStr != null) {
                                return false;
                            }
                        } else if (!this$uidStr.equals(other$uidStr)) {
                            return false;
                        }

                        Object this$geoStr = this.geoStr;
                        Object other$geoStr = other.geoStr;
                        if (this$geoStr == null) {
                            if (other$geoStr != null) {
                                return false;
                            }
                        } else if (!this$geoStr.equals(other$geoStr)) {
                            return false;
                        }

                        return true;
                    }
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SkinEntry;
        }

        public int hashCode() {
            int result = 1;
            result = result * 59 + (this.valid ? 79 : 97);
            Object $uuid = this.uuid;
            result = result * 59 + ($uuid == null ? 43 : $uuid.hashCode());
            result = result * 59 + Arrays.hashCode(this.skinBytes);
            Object $uidStr = this.uidStr;
            result = result * 59 + ($uidStr == null ? 43 : $uidStr.hashCode());
            Object $geoStr = this.geoStr;
            result = result * 59 + ($geoStr == null ? 43 : $geoStr.hashCode());
            return result;
        }
    }
}

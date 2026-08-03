package helper.creeperbox.sdk.network.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;

public class SyncSkinPacket implements BedrockPacket {

    @Override
    public SyncSkinPacket clone() {
        try {
            return (SyncSkinPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private List<SkinEntry> entries = new ArrayList<>();
    private SerializedSkin skin;

    public PacketSignal handle(BedrockPacketHandler handler) {
        return PacketSignal.UNHANDLED;
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.UNKNOWN;
    }

    public String toString() {
        return "SyncSkinPacket(entries=" + this.entries + ", skin=" + skinToString(this.skin) + ")";
    }

    private static String skinToString(SerializedSkin s) {
        if (s == null) return "null";
        return "Skin(id=" + s.getSkinId()
                + ", playFabId=" + s.getPlayFabId()
                + ", skinResourcePatch=" + s.getSkinResourcePatch()
                + ", armSize=" + s.getArmSize()
                + ", skinColor=" + s.getSkinColor()
                + ", premium=" + s.isPremium()
                + ", persona=" + s.isPersona()
                + ", capeOnClassic=" + s.isCapeOnClassic()
                + ", capeId=" + s.getCapeId()
                + ", fullSkinId=" + s.getFullSkinId()
                + ")";
    }

    public SyncSkinPacket() {
    }

    public List<SkinEntry> getEntries() {
        return this.entries;
    }

    public void setEntries(List<SkinEntry> entries) {
        this.entries = entries;
    }

    public SerializedSkin getSkin() {
        return this.skin;
    }

    public void setSkin(SerializedSkin skin) {
        this.skin = skin;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SyncSkinPacket)) {
            return false;
        } else {
            SyncSkinPacket other = (SyncSkinPacket) o;
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

                Object this$skin = this.skin;
                Object other$skin = other.skin;
                if (this$skin == null) {
                    if (other$skin != null) {
                        return false;
                    }
                } else if (!this$skin.equals(other$skin)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SyncSkinPacket;
    }

    public int hashCode() {
        int result = 1;
        Object $entries = this.entries;
        result = result * 59 + ($entries == null ? 43 : $entries.hashCode());
        Object $skin = this.skin;
        result = result * 59 + ($skin == null ? 43 : $skin.hashCode());
        return result;
    }

    public static class SkinEntry {
        private boolean valid;
        private UUID uuid;
        private byte[] skinBytes;
        private String udid;
        private String extraData;
        private String itemId;

        public String toString() {
            boolean var10000 = this.valid;
            return "SyncSkinPacket.SkinEntry(valid=" + var10000
                    + ", uuid=" + this.uuid
                    + ", skinBytes=" + Arrays.toString(this.skinBytes)
                    + ", udid=" + this.udid
                    + ", extraData=" + this.extraData
                    + ", itemId=" + this.itemId + ")";
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

        public String getUdid() {
            return this.udid;
        }

        public String getExtraData() {
            return this.extraData;
        }

        public String getItemId() {
            return this.itemId;
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

        public void setUdid(String udid) {
            this.udid = udid;
        }

        public void setExtraData(String extraData) {
            this.extraData = extraData;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SkinEntry)) {
                return false;
            } else {
                SkinEntry other = (SkinEntry) o;
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
                        Object this$udid = this.udid;
                        Object other$udid = other.udid;
                        if (this$udid == null) {
                            if (other$udid != null) {
                                return false;
                            }
                        } else if (!this$udid.equals(other$udid)) {
                            return false;
                        }

                        Object this$extraData = this.extraData;
                        Object other$extraData = other.extraData;
                        if (this$extraData == null) {
                            if (other$extraData != null) {
                                return false;
                            }
                        } else if (!this$extraData.equals(other$extraData)) {
                            return false;
                        }

                        Object this$itemId = this.itemId;
                        Object other$itemId = other.itemId;
                        if (this$itemId == null) {
                            if (other$itemId != null) {
                                return false;
                            }
                        } else if (!this$itemId.equals(other$itemId)) {
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
            Object $udid = this.udid;
            result = result * 59 + ($udid == null ? 43 : $udid.hashCode());
            Object $extraData = this.extraData;
            result = result * 59 + ($extraData == null ? 43 : $extraData.hashCode());
            Object $itemId = this.itemId;
            result = result * 59 + ($itemId == null ? 43 : $itemId.hashCode());
            return result;
        }
    }
}

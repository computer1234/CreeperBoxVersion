package helper.creeperbox.sdk.network.packet.modify;


import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;

import java.util.List;

public class NeteaseAddEntityPacket extends AddEntityPacket {

    @Override
    public NeteaseAddEntityPacket clone() {
        return (NeteaseAddEntityPacket) super.clone();
    }

    private boolean hasExtra;
    private String extra1;
    private String extra2;
    private String extra3;
    private boolean extra4;
    private boolean extra5;
    private boolean extra6;

    public boolean isHasExtra() {
        return this.hasExtra;
    }

    public String getExtra1() {
        return this.extra1;
    }

    public String getExtra2() {
        return this.extra2;
    }

    public String getExtra3() {
        return this.extra3;
    }

    public boolean isExtra4() {
        return this.extra4;
    }

    public boolean isExtra5() {
        return this.extra5;
    }

    public boolean isExtra6() {
        return this.extra6;
    }

    public void setHasExtra(boolean hasExtra) {
        this.hasExtra = hasExtra;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
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

    public void setExtra5(boolean extra5) {
        this.extra5 = extra5;
    }

    public void setExtra6(boolean extra6) {
        this.extra6 = extra6;
    }


    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseAddEntityPacket)) {
            return false;
        } else {
            NeteaseAddEntityPacket other = (NeteaseAddEntityPacket)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getUniqueEntityId() != other.getUniqueEntityId()) {
                return false;
            } else if (this.getRuntimeEntityId() != other.getRuntimeEntityId()) {
                return false;
            } else if (this.getEntityType() != other.getEntityType()) {
                return false;
            } else if (Float.compare(this.getHeadRotation(), other.getHeadRotation()) != 0) {
                return false;
            } else if (Float.compare(this.getBodyRotation(), other.getBodyRotation()) != 0) {
                return false;
            } else if (this.isHasExtra() != other.isHasExtra()) {
                return false;
            } else if (this.isExtra4() != other.isExtra4()) {
                return false;
            } else if (this.isExtra5() != other.isExtra5()) {
                return false;
            } else if (this.isExtra6() != other.isExtra6()) {
                return false;
            } else {
                Object this$attributes = this.getAttributes();
                Object other$attributes = other.getAttributes();
                if (this$attributes == null) {
                    if (other$attributes != null) {
                        return false;
                    }
                } else if (!this$attributes.equals(other$attributes)) {
                    return false;
                }

                Object this$metadata = this.getMetadata();
                Object other$metadata = other.getMetadata();
                if (this$metadata == null) {
                    if (other$metadata != null) {
                        return false;
                    }
                } else if (!this$metadata.equals(other$metadata)) {
                    return false;
                }

                label151: {
                    Object this$entityLinks = this.getEntityLinks();
                    Object other$entityLinks = other.getEntityLinks();
                    if (this$entityLinks == null) {
                        if (other$entityLinks == null) {
                            break label151;
                        }
                    } else if (this$entityLinks.equals(other$entityLinks)) {
                        break label151;
                    }

                    return false;
                }

                label144: {
                    Object this$identifier = this.getIdentifier();
                    Object other$identifier = other.getIdentifier();
                    if (this$identifier == null) {
                        if (other$identifier == null) {
                            break label144;
                        }
                    } else if (this$identifier.equals(other$identifier)) {
                        break label144;
                    }

                    return false;
                }

                Object this$position = this.getPosition();
                Object other$position = other.getPosition();
                if (this$position == null) {
                    if (other$position != null) {
                        return false;
                    }
                } else if (!this$position.equals(other$position)) {
                    return false;
                }

                Object this$motion = this.getMotion();
                Object other$motion = other.getMotion();
                if (this$motion == null) {
                    if (other$motion != null) {
                        return false;
                    }
                } else if (!this$motion.equals(other$motion)) {
                    return false;
                }

                label123: {
                    Object this$rotation = this.getRotation();
                    Object other$rotation = other.getRotation();
                    if (this$rotation == null) {
                        if (other$rotation == null) {
                            break label123;
                        }
                    } else if (this$rotation.equals(other$rotation)) {
                        break label123;
                    }

                    return false;
                }

                Object this$properties = this.getProperties();
                Object other$properties = other.getProperties();
                if (this$properties == null) {
                    if (other$properties != null) {
                        return false;
                    }
                } else if (!this$properties.equals(other$properties)) {
                    return false;
                }

                Object this$extra1 = this.getExtra1();
                Object other$extra1 = other.getExtra1();
                if (this$extra1 == null) {
                    if (other$extra1 != null) {
                        return false;
                    }
                } else if (!this$extra1.equals(other$extra1)) {
                    return false;
                }

                label102: {
                    Object this$extra2 = this.getExtra2();
                    Object other$extra2 = other.getExtra2();
                    if (this$extra2 == null) {
                        if (other$extra2 == null) {
                            break label102;
                        }
                    } else if (this$extra2.equals(other$extra2)) {
                        break label102;
                    }

                    return false;
                }

                Object this$extra3 = this.getExtra3();
                Object other$extra3 = other.getExtra3();
                if (this$extra3 == null) {
                    if (other$extra3 == null) {
                        return true;
                    }
                } else if (this$extra3.equals(other$extra3)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteaseAddEntityPacket;
    }

    public int hashCode() {
        int result = 1;
        long $uniqueEntityId = this.getUniqueEntityId();
        result = result * 59 + (int)($uniqueEntityId >>> 32 ^ $uniqueEntityId);
        long $runtimeEntityId = this.getRuntimeEntityId();
        result = result * 59 + (int)($runtimeEntityId >>> 32 ^ $runtimeEntityId);
        result = result * 59 + this.getEntityType();
        result = result * 59 + Float.floatToIntBits(this.getHeadRotation());
        result = result * 59 + Float.floatToIntBits(this.getBodyRotation());
        result = result * 59 + (this.isHasExtra() ? 79 : 97);
        result = result * 59 + (this.isExtra4() ? 79 : 97);
        result = result * 59 + (this.isExtra5() ? 79 : 97);
        result = result * 59 + (this.isExtra6() ? 79 : 97);
        Object $attributes = this.getAttributes();
        result = result * 59 + ($attributes == null ? 43 : $attributes.hashCode());
        Object $metadata = this.getMetadata();
        result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
        Object $entityLinks = this.getEntityLinks();
        result = result * 59 + ($entityLinks == null ? 43 : $entityLinks.hashCode());
        Object $identifier = this.getIdentifier();
        result = result * 59 + ($identifier == null ? 43 : $identifier.hashCode());
        Object $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : $position.hashCode());
        Object $motion = this.getMotion();
        result = result * 59 + ($motion == null ? 43 : $motion.hashCode());
        Object $rotation = this.getRotation();
        result = result * 59 + ($rotation == null ? 43 : $rotation.hashCode());
        Object $properties = this.getProperties();
        result = result * 59 + ($properties == null ? 43 : $properties.hashCode());
        Object $extra1 = this.getExtra1();
        result = result * 59 + ($extra1 == null ? 43 : $extra1.hashCode());
        Object $extra2 = this.getExtra2();
        result = result * 59 + ($extra2 == null ? 43 : $extra2.hashCode());
        Object $extra3 = this.getExtra3();
        result = result * 59 + ($extra3 == null ? 43 : $extra3.hashCode());
        return result;
    }

    public String toString() {
        List var10000 = this.getAttributes();
        return "NeteaseAddEntityPacket(attributes=" + var10000 + ", metadata=" + this.getMetadata() + ", entityLinks=" + this.getEntityLinks() + ", uniqueEntityId=" + this.getUniqueEntityId() + ", runtimeEntityId=" + this.getRuntimeEntityId() + ", identifier=" + this.getIdentifier() + ", entityType=" + this.getEntityType() + ", position=" + this.getPosition() + ", motion=" + this.getMotion() + ", rotation=" + this.getRotation() + ", headRotation=" + this.getHeadRotation() + ", bodyRotation=" + this.getBodyRotation() + ", properties=" + this.getProperties() + ", hasExtra=" + this.isHasExtra() + ", extra1=" + this.getExtra1() + ", extra2=" + this.getExtra2() + ", extra3=" + this.getExtra3() + ", extra4=" + this.isExtra4() + ", extra5=" + this.isExtra5() + ", extra6=" + this.isExtra6() + ")";
    }


}

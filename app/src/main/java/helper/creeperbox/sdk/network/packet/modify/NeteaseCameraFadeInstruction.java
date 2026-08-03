package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.protocol.bedrock.data.camera.CameraFadeInstruction;

public class NeteaseCameraFadeInstruction {

    private CameraFadeInstruction.TimeData timeData;
    private int color;

    public CameraFadeInstruction.TimeData getTimeData() {
        return this.timeData;
    }

    public int getColor() {
        return this.color;
    }

    public void setTimeData(CameraFadeInstruction.TimeData timeData) {
        this.timeData = timeData;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseCameraFadeInstruction)) {
            return false;
        } else {
            NeteaseCameraFadeInstruction other = (NeteaseCameraFadeInstruction)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$timeData = this.getTimeData();
                Object other$timeData = other.getTimeData();
                if (this$timeData == null) {
                    if (other$timeData != null) {
                        return false;
                    }
                } else if (!this$timeData.equals(other$timeData)) {
                    return false;
                }

                Object this$color = this.getColor();
                Object other$color = other.getColor();
                if (this$color == null) {
                    if (other$color != null) {
                        return false;
                    }
                } else if (!this$color.equals(other$color)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteaseCameraFadeInstruction;
    }

    public int hashCode() {
        int result = 1;
        Object $timeData = this.getTimeData();
        result = result * 59 + ($timeData == null ? 43 : $timeData.hashCode());
        Object $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : $color.hashCode());
        return result;
    }

    public String toString() {
        return "NeteaseCameraFadeInstruction(timeData=" + this.getTimeData() + ", color=" + this.getColor() + ")";
    }

    public NeteaseCameraFadeInstruction(CameraFadeInstruction.TimeData timeData, int color) {
        this.timeData = timeData;
        this.color = color;
    }

    public NeteaseCameraFadeInstruction() {
    }

    public static class TimeData {
        private final float fadeInTime;
        private final float waitTime;
        private final float fadeOutTime;

        public TimeData(float fadeInTime, float waitTime, float fadeOutTime) {
            this.fadeInTime = fadeInTime;
            this.waitTime = waitTime;
            this.fadeOutTime = fadeOutTime;
        }

        public float getFadeInTime() {
            return this.fadeInTime;
        }

        public float getWaitTime() {
            return this.waitTime;
        }

        public float getFadeOutTime() {
            return this.fadeOutTime;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof NeteaseCameraFadeInstruction.TimeData)) {
                return false;
            } else {
                NeteaseCameraFadeInstruction.TimeData other = (NeteaseCameraFadeInstruction.TimeData)o;
                if (!other.canEqual(this)) {
                    return false;
                } else if (Float.compare(this.getFadeInTime(), other.getFadeInTime()) != 0) {
                    return false;
                } else if (Float.compare(this.getWaitTime(), other.getWaitTime()) != 0) {
                    return false;
                } else {
                    return Float.compare(this.getFadeOutTime(), other.getFadeOutTime()) == 0;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof NeteaseCameraFadeInstruction.TimeData;
        }

        public int hashCode() {
            int result = 1;
            result = result * 59 + Float.floatToIntBits(this.getFadeInTime());
            result = result * 59 + Float.floatToIntBits(this.getWaitTime());
            result = result * 59 + Float.floatToIntBits(this.getFadeOutTime());
            return result;
        }

        public String toString() {
            return "NeteaseCameraFadeInstruction.TimeData(fadeInTime=" + this.getFadeInTime() + ", waitTime=" + this.getWaitTime() + ", fadeOutTime=" + this.getFadeOutTime() + ")";
        }
    }


}

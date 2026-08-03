package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;

public class NeteasePlayerAuthInputPacket extends PlayerAuthInputPacket {

    @Override
    public NeteasePlayerAuthInputPacket clone() {
        return (NeteasePlayerAuthInputPacket) super.clone();
    }

    private boolean hasExtra;
    private boolean cameraDeparted;
    private boolean thirdPersonPerspective;
    private Vector2f playerRotationToCamera;
    private boolean readyPosDetalDirty;

    private boolean isOnGround;

    private byte resetPosition;

    // 末尾额外的字节
    private byte[] extraBytes;

    public byte[] getExtraBytes() {
        return extraBytes;
    }

    public void setExtraBytes(byte[] extraBytes) {
        this.extraBytes = extraBytes;
    }

    public boolean isReadyPosDetalDirty() {
        return readyPosDetalDirty;
    }

    public void setReadyPosDetalDirty(boolean readyPosDetalDirty) {
        this.readyPosDetalDirty = readyPosDetalDirty;
    }

    public byte getResetPosition() {
        return resetPosition;
    }

    public void setResetPosition(byte resetPosition) {
        this.resetPosition = resetPosition;
    }

    public boolean isCameraDeparted() {
        return cameraDeparted;
    }

    public void setCameraDeparted(boolean cameraDeparted) {
        this.cameraDeparted = cameraDeparted;
    }

    public void setOnGround(boolean onGround) {
        this.isOnGround = onGround;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public boolean isHasExtra() {
        return this.hasExtra;
    }

    public void setHasExtra(boolean hasExtra) {
        this.hasExtra = hasExtra;
    }

    public boolean isThirdPersonPerspective() {
        return this.thirdPersonPerspective;
    }

    public void setThirdPersonPerspective(boolean thirdPersonPerspective) {
        this.thirdPersonPerspective = thirdPersonPerspective;
    }

    public Vector2f getPlayerRotationToCamera() {
        return playerRotationToCamera;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteasePlayerAuthInputPacket)) {
            return false;
        } else {
            NeteasePlayerAuthInputPacket other = (NeteasePlayerAuthInputPacket) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getTick() != other.getTick()) {
                return false;
            } else if (this.getPredictedVehicle() != other.getPredictedVehicle()) {
                return false;
            } else if (this.isHasExtra() != other.isHasExtra()) {
                return false;
            } else if (this.isCameraDeparted() != other.isCameraDeparted()) {
                return false;
            } else if (this.isThirdPersonPerspective() != other.isThirdPersonPerspective()) {
                return false;
            } else if (this.isReadyPosDetalDirty() != other.isReadyPosDetalDirty()) {
                return false;
            } else if (this.isOnGround() != other.isOnGround()) {
                return false;
            } else if (this.getResetPosition() != other.getResetPosition()) {
                return false;
            } else {
                Object this$playerRotationToCamera = this.getPlayerRotationToCamera();
                Object other$playerRotationToCamera = other.getPlayerRotationToCamera();
                if (this$playerRotationToCamera == null) {
                    if (other$playerRotationToCamera != null) {
                        return false;
                    }
                } else if (!this$playerRotationToCamera.equals(other$playerRotationToCamera)) {
                    return false;
                }

                Object this$rotation = this.getRotation();
                Object other$rotation = other.getRotation();
                if (this$rotation == null) {
                    if (other$rotation != null) {
                        return false;
                    }
                } else if (!this$rotation.equals(other$rotation)) {
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

                Object this$inputData = this.getInputData();
                Object other$inputData = other.getInputData();
                if (this$inputData == null) {
                    if (other$inputData != null) {
                        return false;
                    }
                } else if (!this$inputData.equals(other$inputData)) {
                    return false;
                }

                Object this$inputMode = this.getInputMode();
                Object other$inputMode = other.getInputMode();
                if (this$inputMode == null) {
                    if (other$inputMode != null) {
                        return false;
                    }
                } else if (!this$inputMode.equals(other$inputMode)) {
                    return false;
                }

                Object this$playMode = this.getPlayMode();
                Object other$playMode = other.getPlayMode();
                if (this$playMode == null) {
                    if (other$playMode != null) {
                        return false;
                    }
                } else if (!this$playMode.equals(other$playMode)) {
                    return false;
                }

                Object this$vrGazeDirection = this.getVrGazeDirection();
                Object other$vrGazeDirection = other.getVrGazeDirection();
                if (this$vrGazeDirection == null) {
                    if (other$vrGazeDirection != null) {
                        return false;
                    }
                } else if (!this$vrGazeDirection.equals(other$vrGazeDirection)) {
                    return false;
                }

                Object this$delta = this.getDelta();
                Object other$delta = other.getDelta();
                if (this$delta == null) {
                    if (other$delta != null) {
                        return false;
                    }
                } else if (!this$delta.equals(other$delta)) {
                    return false;
                }

                Object this$itemUseTransaction = this.getItemUseTransaction();
                Object other$itemUseTransaction = other.getItemUseTransaction();
                if (this$itemUseTransaction == null) {
                    if (other$itemUseTransaction != null) {
                        return false;
                    }
                } else if (!this$itemUseTransaction.equals(other$itemUseTransaction)) {
                    return false;
                }

                Object this$itemStackRequest = this.getItemStackRequest();
                Object other$itemStackRequest = other.getItemStackRequest();
                if (this$itemStackRequest == null) {
                    if (other$itemStackRequest != null) {
                        return false;
                    }
                } else if (!this$itemStackRequest.equals(other$itemStackRequest)) {
                    return false;
                }

                Object this$playerActions = this.getPlayerActions();
                Object other$playerActions = other.getPlayerActions();
                if (this$playerActions == null) {
                    if (other$playerActions != null) {
                        return false;
                    }
                } else if (!this$playerActions.equals(other$playerActions)) {
                    return false;
                }

                Object this$inputInteractionModel = this.getInputInteractionModel();
                Object other$inputInteractionModel = other.getInputInteractionModel();
                if (this$inputInteractionModel == null) {
                    if (other$inputInteractionModel != null) {
                        return false;
                    }
                } else if (!this$inputInteractionModel.equals(other$inputInteractionModel)) {
                    return false;
                }

                Object this$analogMoveVector = this.getAnalogMoveVector();
                Object other$analogMoveVector = other.getAnalogMoveVector();
                if (this$analogMoveVector == null) {
                    if (other$analogMoveVector != null) {
                        return false;
                    }
                } else if (!this$analogMoveVector.equals(other$analogMoveVector)) {
                    return false;
                }

                Object this$vehicleRotation = this.getVehicleRotation();
                Object other$vehicleRotation = other.getVehicleRotation();
                if (this$vehicleRotation == null) {
                    if (other$vehicleRotation != null) {
                        return false;
                    }
                } else if (!this$vehicleRotation.equals(other$vehicleRotation)) {
                    return false;
                }

                return true;
            }
        }
    }

    public void setPlayerRotationToCamera(Vector2f playerRotationToCamera) {
        this.playerRotationToCamera = playerRotationToCamera;
    }

    protected boolean canEqual(Object other) {
        return other instanceof NeteasePlayerAuthInputPacket;
    }

    public int hashCode() {
        int result = 1;
        long $tick = this.getTick();
        result = result * 59 + (int) ($tick >>> 32 ^ $tick);
        long $predictedVehicle = this.getPredictedVehicle();
        result = result * 59 + (int) ($predictedVehicle >>> 32 ^ $predictedVehicle);
        result = result * 59 + (this.isHasExtra() ? 79 : 97);
        result = result * 59 + (this.isCameraDeparted() ? 79 : 97);
        result = result * 59 + (this.isThirdPersonPerspective() ? 79 : 97);
        result = result * 59 + (this.isReadyPosDetalDirty() ? 79 : 97);
        result = result * 59 + (this.isOnGround() ? 79 : 97);
        result = result * 59 + this.getResetPosition();
        Object $playerRotationToCamera = this.getPlayerRotationToCamera();
        result = result * 59 + ($playerRotationToCamera == null ? 43 : $playerRotationToCamera.hashCode());
        Object $rotation = this.getRotation();
        result = result * 59 + ($rotation == null ? 43 : $rotation.hashCode());
        Object $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : $position.hashCode());
        Object $motion = this.getMotion();
        result = result * 59 + ($motion == null ? 43 : $motion.hashCode());
        Object $inputData = this.getInputData();
        result = result * 59 + ($inputData == null ? 43 : $inputData.hashCode());
        Object $inputMode = this.getInputMode();
        result = result * 59 + ($inputMode == null ? 43 : $inputMode.hashCode());
        Object $playMode = this.getPlayMode();
        result = result * 59 + ($playMode == null ? 43 : $playMode.hashCode());
        Object $vrGazeDirection = this.getVrGazeDirection();
        result = result * 59 + ($vrGazeDirection == null ? 43 : $vrGazeDirection.hashCode());
        Object $delta = this.getDelta();
        result = result * 59 + ($delta == null ? 43 : $delta.hashCode());
        Object $itemUseTransaction = this.getItemUseTransaction();
        result = result * 59 + ($itemUseTransaction == null ? 43 : $itemUseTransaction.hashCode());
        Object $itemStackRequest = this.getItemStackRequest();
        result = result * 59 + ($itemStackRequest == null ? 43 : $itemStackRequest.hashCode());
        Object $playerActions = this.getPlayerActions();
        result = result * 59 + ($playerActions == null ? 43 : $playerActions.hashCode());
        Object $inputInteractionModel = this.getInputInteractionModel();
        result = result * 59 + ($inputInteractionModel == null ? 43 : $inputInteractionModel.hashCode());
        Object $analogMoveVector = this.getAnalogMoveVector();
        result = result * 59 + ($analogMoveVector == null ? 43 : $analogMoveVector.hashCode());
        Object $vehicleRotation = this.getVehicleRotation();
        result = result * 59 + ($vehicleRotation == null ? 43 : $vehicleRotation.hashCode());
        return result;
    }

    public String toString() {
        return "NeteasePlayerAuthInputPacket{" +
                "tick=" + getTick() +
                ", position=" + getPosition() +
                ", rotation=" + getRotation() +
                ", motion=" + getMotion() +
                ", delta=" + getDelta() +
                ", inputData=" + getInputData() +
                ", inputMode=" + getInputMode() +
                ", playMode=" + getPlayMode() +
                ", inputInteractionModel=" + getInputInteractionModel() +
                ", analogMoveVector=" + getAnalogMoveVector() +
                ", cameraOrientation=" + getCameraOrientation() +
                ", rawMoveVector=" + getRawMoveVector() +
                ", vrGazeDirection=" + getVrGazeDirection() +
                ", predictedVehicle=" + getPredictedVehicle() +
                ", vehicleRotation=" + getVehicleRotation() +
                ", itemUseTransaction=" + getItemUseTransaction() +
                ", itemStackRequest=" + getItemStackRequest() +
                ", playerActions=" + getPlayerActions() +
                ", cameraDeparted=" + cameraDeparted +
                ", hasExtra=" + hasExtra +
                ", thirdPersonPerspective=" + thirdPersonPerspective +
                ", playerRotationToCamera=" + playerRotationToCamera +
                ", readyPosDetalDirty=" + readyPosDetalDirty +
                ", isOnGround=" + isOnGround +
                ", resetPosition=" + resetPosition +
                '}';
    }


}

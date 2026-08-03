package helper.creeperbox.sdk.network.serializer;

import android.util.Log;

import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v419.serializer.PlayerAuthInputSerializer_v419;
import org.cloudburstmc.protocol.bedrock.data.InputInteractionModel;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.PlayerBlockActionData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.ItemUseTransaction;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.LegacySetItemSlotData;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.cloudburstmc.protocol.common.util.VarInts;

public class PlayerAuthInputSerializer extends PlayerAuthInputSerializer_v419 {
    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, PlayerAuthInputPacket packet) {
        // v388 部分
        org.cloudburstmc.math.vector.Vector3f rotation = packet.getRotation();
        buffer.writeFloatLE(rotation.getX());
        buffer.writeFloatLE(rotation.getY());
        helper.writeVector3f(buffer, packet.getPosition());
        buffer.writeFloatLE(packet.getMotion().getX());
        buffer.writeFloatLE(packet.getMotion().getY());
        buffer.writeFloatLE(rotation.getZ());

        // 网易 inputData 固定 8 字节
        long flagValue = 0;
        for (PlayerAuthInputData data : packet.getInputData()) {
            flagValue |= (1L << data.ordinal());
        }
        buffer.writeLongLE(flagValue);

        VarInts.writeUnsignedInt(buffer, packet.getInputMode().ordinal());
        VarInts.writeUnsignedInt(buffer, packet.getPlayMode().ordinal());
        writeInteractionModel(buffer, helper, packet);
        helper.writeVector2f(buffer, packet.getInteractRotation());

        if (packet.getPlayMode() == org.cloudburstmc.protocol.bedrock.data.ClientPlayMode.REALITY) {
            helper.writeVector3f(buffer, packet.getVrGazeDirection());
        }

        // v419 部分
        VarInts.writeUnsignedLong(buffer, packet.getTick());
        helper.writeVector3f(buffer, packet.getDelta());

        NeteasePlayerAuthInputPacket newPacket = (NeteasePlayerAuthInputPacket) packet;
        buffer.writeBoolean(newPacket.isCameraDeparted());

        if (packet.getInputData().contains(PlayerAuthInputData.PERFORM_ITEM_INTERACTION)) {
            ItemUseTransaction transaction = packet.getItemUseTransaction();
            int legacyRequestId = transaction.getLegacyRequestId();
            VarInts.writeInt(buffer, legacyRequestId);

            if (legacyRequestId < -1 && (legacyRequestId & 1) == 0) {
                helper.writeArray(buffer, transaction.getLegacySlots(), (buf, packetHelper, data) -> {
                    buf.writeByte(data.getContainerId());
                    packetHelper.writeByteArray(buf, data.getSlots());
                });
            }

            helper.writeInventoryActions(buffer, transaction.getActions(), transaction.isUsingNetIds());
            VarInts.writeUnsignedInt(buffer, transaction.getActionType());
            helper.writeBlockPosition(buffer, transaction.getBlockPosition());
            VarInts.writeInt(buffer, transaction.getBlockFace());
            VarInts.writeInt(buffer, transaction.getHotbarSlot());
            helper.writeItem(buffer, transaction.getItemInHand());
            helper.writeVector3f(buffer, transaction.getPlayerPosition());
            helper.writeVector3f(buffer, transaction.getClickPosition());
            VarInts.writeUnsignedInt(buffer, transaction.getBlockDefinition().getRuntimeId());
        }

        if (packet.getInputData().contains(PlayerAuthInputData.PERFORM_ITEM_STACK_REQUEST)) {
            helper.writeItemStackRequest(buffer, packet.getItemStackRequest());
        }

        if (packet.getInputData().contains(PlayerAuthInputData.PERFORM_BLOCK_ACTIONS)) {
            VarInts.writeInt(buffer, packet.getPlayerActions().size());
            for (PlayerBlockActionData actionData : packet.getPlayerActions()) {
                writePlayerBlockActionData(buffer, helper, actionData);
            }
        }

        if (packet.getInputData().contains(PlayerAuthInputData.IN_CLIENT_PREDICTED_IN_VEHICLE)){
            helper.writeVector2f(buffer, packet.getVehicleRotation());
            VarInts.writeLong(buffer, packet.getPredictedVehicle());
        }

        helper.writeVector2f(buffer, packet.getAnalogMoveVector());
        helper.writeVector3f(buffer, packet.getCameraOrientation());
        helper.writeVector2f(buffer, packet.getRawMoveVector());

        if(newPacket.isHasExtra()) {
            buffer.writeBoolean(newPacket.isThirdPersonPerspective());
            helper.writeVector2f(buffer, newPacket.getPlayerRotationToCamera());
            buffer.writeBoolean(newPacket.isReadyPosDetalDirty());
            buffer.writeBoolean(newPacket.isOnGround());
            buffer.writeByte(newPacket.getResetPosition());
        }
        if(newPacket.getExtraBytes() != null && newPacket.getExtraBytes().length > 0) {
            buffer.writeBytes(newPacket.getExtraBytes());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, PlayerAuthInputPacket packet) {
        // v388 部分
        float rotX = buffer.readFloatLE();
        float rotY = buffer.readFloatLE();
        packet.setPosition(helper.readVector3f(buffer));
        packet.setMotion(org.cloudburstmc.math.vector.Vector2f.from(buffer.readFloatLE(), buffer.readFloatLE()));
        float rotZ = buffer.readFloatLE();
        packet.setRotation(org.cloudburstmc.math.vector.Vector3f.from(rotX, rotY, rotZ));

        // 网易 inputData 固定 8 字节
        long flagValue = buffer.readLongLE();

        java.util.Set<PlayerAuthInputData> flags = packet.getInputData();
        for (PlayerAuthInputData flag : PlayerAuthInputData.values()) {
            if ((flagValue & (1L << flag.ordinal())) != 0) {
                flags.add(flag);
            }
        }

        packet.setInputMode(org.cloudburstmc.protocol.bedrock.data.InputMode.values()[VarInts.readUnsignedInt(buffer)]);
        packet.setPlayMode(org.cloudburstmc.protocol.bedrock.data.ClientPlayMode.values()[VarInts.readUnsignedInt(buffer)]);
        readInteractionModel(buffer, helper, packet);
        packet.setInteractRotation(helper.readVector2f(buffer));

        if (packet.getPlayMode() == org.cloudburstmc.protocol.bedrock.data.ClientPlayMode.REALITY) {
            packet.setVrGazeDirection(helper.readVector3f(buffer));
        }

        // v419 部分
        packet.setTick(VarInts.readUnsignedLong(buffer));
        packet.setDelta(helper.readVector3f(buffer));

        NeteasePlayerAuthInputPacket newPacket = (NeteasePlayerAuthInputPacket) packet;
        newPacket.setCameraDeparted(buffer.readBoolean());

        if (packet.getInputData().contains(PlayerAuthInputData.PERFORM_ITEM_INTERACTION)) {
            ItemUseTransaction itemTransaction = new ItemUseTransaction();

            int legacyRequestId = VarInts.readInt(buffer);
            itemTransaction.setLegacyRequestId(legacyRequestId);

            if (legacyRequestId < -1 && (legacyRequestId & 1) == 0) {
                helper.readArray(buffer, itemTransaction.getLegacySlots(), (buf, packetHelper) -> {
                    byte containerId = buf.readByte();
                    byte[] slots = packetHelper.readByteArray(buf);
                    return new LegacySetItemSlotData(containerId, slots);
                });
            }

            helper.readInventoryActions(buffer, itemTransaction.getActions());
            itemTransaction.setActionType(VarInts.readUnsignedInt(buffer));
            itemTransaction.setBlockPosition(helper.readBlockPosition(buffer));
            itemTransaction.setBlockFace(VarInts.readInt(buffer));
            itemTransaction.setHotbarSlot(VarInts.readInt(buffer));
            itemTransaction.setItemInHand(helper.readItem(buffer));
            itemTransaction.setPlayerPosition(helper.readVector3f(buffer));
            itemTransaction.setClickPosition(helper.readVector3f(buffer));
            itemTransaction.setBlockDefinition(helper.getBlockDefinitions().getDefinition(VarInts.readUnsignedInt(buffer)));
            packet.setItemUseTransaction(itemTransaction);
        }

        if (packet.getInputData().contains(PlayerAuthInputData.PERFORM_ITEM_STACK_REQUEST)) {
            packet.setItemStackRequest(helper.readItemStackRequest(buffer));
        }

        if (packet.getInputData().contains(PlayerAuthInputData.PERFORM_BLOCK_ACTIONS)) {
            int arraySize = VarInts.readInt(buffer);
            for (int i = 0; i < arraySize; i++) {
                packet.getPlayerActions().add(readPlayerBlockActionData(buffer, helper));
            }
        }


        if (packet.getInputData().contains(PlayerAuthInputData.IN_CLIENT_PREDICTED_IN_VEHICLE)) {
            packet.setVehicleRotation(helper.readVector2f(buffer));
            packet.setPredictedVehicle(VarInts.readLong(buffer));
        }

        packet.setAnalogMoveVector(helper.readVector2f(buffer));
        packet.setCameraOrientation(helper.readVector3f(buffer));
        packet.setRawMoveVector(helper.readVector2f(buffer));

        newPacket.setHasExtra(buffer.readableBytes() != 0);
        if(newPacket.isHasExtra()) {
            newPacket.setThirdPersonPerspective(buffer.readBoolean());
            newPacket.setPlayerRotationToCamera(helper.readVector2f(buffer));
            newPacket.setReadyPosDetalDirty(buffer.readBoolean());
            newPacket.setOnGround(buffer.readBoolean());
            newPacket.setResetPosition(buffer.readByte());
        }
        int remaining = buffer.readableBytes();
        if(remaining > 0) {
            byte[] extraBytes = new byte[remaining];
            buffer.readBytes(extraBytes);
            newPacket.setExtraBytes(extraBytes);
        }
    }

    protected static final InputInteractionModel[] VALUES = InputInteractionModel.values();

    @Override
    protected void readInteractionModel(ByteBuf buffer, BedrockCodecHelper helper, PlayerAuthInputPacket packet) {
        packet.setInputInteractionModel(VALUES[VarInts.readUnsignedInt(buffer)]);
    }

    @Override
    protected void writeInteractionModel(ByteBuf buffer, BedrockCodecHelper helper, PlayerAuthInputPacket packet) {
        VarInts.writeUnsignedInt(buffer, packet.getInputInteractionModel().ordinal());
    }

    protected void writePlayerBlockActionData(ByteBuf buffer, BedrockCodecHelper helper, PlayerBlockActionData actionData) {
        VarInts.writeInt(buffer, actionData.getAction().ordinal());
        switch (actionData.getAction()) {
            case START_BREAK:
            case ABORT_BREAK:
            case CONTINUE_BREAK:
            case BLOCK_PREDICT_DESTROY:
            case BLOCK_CONTINUE_DESTROY:
                helper.writeVector3i(buffer, actionData.getBlockPosition());
                VarInts.writeInt(buffer, actionData.getFace());
        }
    }

    protected PlayerBlockActionData readPlayerBlockActionData(ByteBuf buffer, BedrockCodecHelper helper) {
        PlayerBlockActionData actionData = new PlayerBlockActionData();
        actionData.setAction(PlayerActionType.values()[VarInts.readInt(buffer)]);
        switch (actionData.getAction()) {
            case START_BREAK:
            case ABORT_BREAK:
            case CONTINUE_BREAK:
            case BLOCK_PREDICT_DESTROY:
            case BLOCK_CONTINUE_DESTROY:
                actionData.setBlockPosition(helper.readVector3i(buffer));
                actionData.setFace(VarInts.readInt(buffer));
        }
        return actionData;
    }
}

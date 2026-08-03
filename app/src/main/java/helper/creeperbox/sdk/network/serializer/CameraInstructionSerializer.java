package helper.creeperbox.sdk.network.serializer;


import android.graphics.Color;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraEase;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraFadeInstruction;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraSetInstruction;
import org.cloudburstmc.protocol.common.NamedDefinition;
import org.cloudburstmc.protocol.common.util.DefinitionUtils;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.cloudburstmc.protocol.common.util.Preconditions;

import java.util.List;

import helper.creeperbox.sdk.network.packet.modify.NeteaseCameraFadeInstruction;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCameraInstructionPacket;
import io.netty.buffer.ByteBuf;

public class CameraInstructionSerializer implements BedrockPacketSerializer<NeteaseCameraInstructionPacket> {
    public CameraInstructionSerializer() {
    }

    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseCameraInstructionPacket packet) {
        NbtMapBuilder tag = NbtMap.builder();
        NbtMapBuilder builder;
        if (packet.getSetInstruction() != null) {
            CameraSetInstruction set = packet.getSetInstruction();
            DefinitionUtils.checkDefinition(helper.getCameraPresetDefinitions(), set.getPreset());
            builder = NbtMap.builder().putInt("preset", set.getPreset().getRuntimeId());
            if (set.getEase() != null) {
                builder.putCompound("ease", NbtMap.builder().putString("type", set.getEase().getEaseType().getSerializeName()).putFloat("time", set.getEase().getTime()).build());
            }

            if (set.getPos() != null) {
                builder.putCompound("pos", NbtMap.builder().putList("pos", NbtType.FLOAT, new Float[]{set.getPos().getX(), set.getPos().getY(), set.getPos().getZ()}).build());
            }

            if (set.getRot() != null) {
                builder.putCompound("rot", NbtMap.builder().putFloat("x", set.getRot().getX()).putFloat("y", set.getRot().getY()).build());
            }

            if (set.getDefaultPreset().isPresent()) {
                builder.putBoolean("default", set.getDefaultPreset().getAsBoolean());
            }

            tag.put("set", builder.build());
        }

        if (packet.getClear().isPresent()) {
            tag.putBoolean("clear", packet.getClear().getAsBoolean());
        }

        if (packet.getFadeInstruction() != null) {
            NeteaseCameraFadeInstruction fade = packet.getFadeInstruction();
            builder = NbtMap.builder();
            if (fade.getTimeData() != null) {
                builder.putCompound("time", NbtMap.builder().putFloat("fadeIn", fade.getTimeData().getFadeInTime()).putFloat("hold", fade.getTimeData().getWaitTime()).putFloat("fadeOut", fade.getTimeData().getFadeOutTime()).build());
            }

            if (fade.getColor() != 0) {
                builder.putCompound("color", NbtMap.builder().putFloat("r", (float) Color.red(fade.getColor()) / 255.0F).putFloat("g", (float)Color.green(fade.getColor()) / 255.0F).putFloat("b", (float)Color.blue(fade.getColor()) / 255.0F).build());
            }

            tag.put("fade", builder.build());
        }

        helper.writeTag(buffer, tag.build());
    }

    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, NeteaseCameraInstructionPacket packet) {
        NbtMap tag = (NbtMap)helper.readTag(buffer, NbtMap.class);
        NbtMap setTag;
        float fadeout;
        if (tag.containsKey("set", NbtType.COMPOUND)) {
            CameraSetInstruction set = new CameraSetInstruction();
            setTag = tag.getCompound("set");
            int runtimeId = setTag.getInt("preset");
            NamedDefinition definition = (NamedDefinition)helper.getCameraPresetDefinitions().getDefinition(runtimeId);
            Preconditions.checkNotNull(definition, "Unknown camera preset " + runtimeId);
            set.setPreset(definition);
            NbtMap rot;
            float yaw;
            if (setTag.containsKey("ease", NbtType.COMPOUND)) {
                rot = setTag.getCompound("ease");
                CameraEase type = CameraEase.fromName(rot.getString("type"));
                yaw = rot.getFloat("time");
                set.setEase(new CameraSetInstruction.EaseData(type, yaw));
            }

            if (setTag.containsKey("pos", NbtType.COMPOUND)) {
                List<Float> floats = setTag.getCompound("pos").getList("pos", NbtType.FLOAT);
                fadeout = floats.size() > 0 ? (Float)floats.get(0) : 0.0F;
                yaw = floats.size() > 1 ? (Float)floats.get(1) : 0.0F;
                float z = floats.size() > 2 ? (Float)floats.get(2) : 0.0F;
                set.setPos(Vector3f.from(fadeout, yaw, z));
            }

            if (setTag.containsKey("rot", NbtType.COMPOUND)) {
                rot = setTag.getCompound("rot");
                fadeout = rot.containsKey("x", NbtType.FLOAT) ? rot.getFloat("x") : 0.0F;
                yaw = rot.containsKey("y", NbtType.FLOAT) ? rot.getFloat("y") : 0.0F;
                set.setRot(Vector2f.from(fadeout, yaw));
            }

            if (setTag.containsKey("default", NbtType.BYTE)) {
                set.setDefaultPreset(OptionalBoolean.of(setTag.getBoolean("default")));
            }

            packet.setSetInstruction(set);
        }

        if (tag.containsKey("clear", NbtType.BYTE)) {
            packet.setClear(OptionalBoolean.of(tag.getBoolean("clear")));
        }

        if (tag.containsKey("fade", NbtType.COMPOUND)) {
            NeteaseCameraFadeInstruction fade = new NeteaseCameraFadeInstruction();
            setTag = tag.getCompound("fade");
            NbtMap colorTag;
            if (setTag.containsKey("time", NbtType.COMPOUND)) {
                colorTag = setTag.getCompound("time");
                float fadeIn = colorTag.getFloat("fadeIn");
                float wait = colorTag.getFloat("hold");
                fadeout = colorTag.getFloat("fadeOut");
                fade.setTimeData(new CameraFadeInstruction.TimeData(fadeIn, wait, fadeout));
            }

            if (setTag.containsKey("color", NbtType.COMPOUND)) {
                colorTag = tag.getCompound("color");
                fade.setColor(Color.rgb((int)(colorTag.getFloat("r") * 255.0F), (int)(colorTag.getFloat("g") * 255.0F), (int)(colorTag.getFloat("b") * 255.0F)));
            }

            packet.setFadeInstruction(fade);
        }
    }


}

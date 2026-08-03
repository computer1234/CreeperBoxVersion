package helper.creeperbox.sdk.network.codec;

import helper.creeperbox.sdk.network.packet.ConfirmSkinPacket;
import helper.creeperbox.sdk.network.packet.NeteaseJsonPacket;
import helper.creeperbox.sdk.network.packet.PyRpcPacket;
import helper.creeperbox.sdk.network.packet.SyncSkinPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseAddEntityPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseAnimatePacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCameraInstructionPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCommandRequestPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseContainerOpenPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseResourcePackStackPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseSetPlayerInventoryOptionsPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseStartGamePacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;
import helper.creeperbox.sdk.network.serializer.AddEntitySerializer;
import helper.creeperbox.sdk.network.serializer.AnimateSerializer;
import helper.creeperbox.sdk.network.serializer.CameraInstructionSerializer;
import helper.creeperbox.sdk.network.serializer.CommandRequestSerializer;
import helper.creeperbox.sdk.network.serializer.ConfirmSkinSerializer;
import helper.creeperbox.sdk.network.serializer.SyncSkinSerializer;
import helper.creeperbox.sdk.network.serializer.ContainerOpenSerializer;
import helper.creeperbox.sdk.network.serializer.NeteaseJsonSerializer;
import helper.creeperbox.sdk.network.serializer.NeteaseTextSerializer;
import helper.creeperbox.sdk.network.serializer.PlayerAuthInputSerializer;
import helper.creeperbox.sdk.network.serializer.PyRpcSerializer;
import helper.creeperbox.sdk.network.serializer.ResourcePackStackSerializer;
import helper.creeperbox.sdk.network.serializer.SetPlayerInventoryOptionsSerializer;
import helper.creeperbox.sdk.network.serializer.StartGameSerializer;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v662.Bedrock_v662;
import org.cloudburstmc.protocol.bedrock.codec.v686.Bedrock_v686;
import org.cloudburstmc.protocol.bedrock.codec.v766.Bedrock_v766;
import org.cloudburstmc.protocol.bedrock.data.PacketRecipient;
import org.cloudburstmc.protocol.bedrock.packet.*;

public class NeteaseBedrock extends Bedrock_v766 {

    public static final BedrockCodec CODEC = Bedrock_v766.CODEC.toBuilder()
            .registerPacket(NeteaseJsonPacket::new, NeteaseJsonSerializer.INSTANCE,203, PacketRecipient.BOTH)
            .registerPacket(ConfirmSkinPacket::new, ConfirmSkinSerializer.INSTANCE,228, PacketRecipient.BOTH)
            .registerPacket(SyncSkinPacket::new, SyncSkinSerializer.INSTANCE,236, PacketRecipient.BOTH)
            .registerPacket(PyRpcPacket::new, PyRpcSerializer.INSTANCE,200, PacketRecipient.BOTH)
            .deregisterPacket(CommandRequestPacket.class)
            .registerPacket(NeteaseCommandRequestPacket::new,new CommandRequestSerializer(),77, PacketRecipient.BOTH)
            .deregisterPacket(AnimatePacket.class)
            .registerPacket(NeteaseAnimatePacket::new,new AnimateSerializer(),44, PacketRecipient.BOTH)
            .deregisterPacket(ContainerOpenPacket.class)
            .registerPacket(NeteaseContainerOpenPacket::new,new ContainerOpenSerializer(),46, PacketRecipient.BOTH)
            .deregisterPacket(AddEntityPacket.class)
            .registerPacket(NeteaseAddEntityPacket::new,new AddEntitySerializer(),13, PacketRecipient.BOTH)
            .deregisterPacket(PlayerAuthInputPacket.class)
            .registerPacket(NeteasePlayerAuthInputPacket::new,new PlayerAuthInputSerializer(),144, PacketRecipient.BOTH)
            .deregisterPacket(StartGamePacket.class)
            .registerPacket(NeteaseStartGamePacket::new,new StartGameSerializer(),11, PacketRecipient.BOTH)
            .deregisterPacket(CameraInstructionPacket.class)
            .registerPacket(NeteaseCameraInstructionPacket::new,new CameraInstructionSerializer(),300, PacketRecipient.BOTH)
            .deregisterPacket(TextPacket.class)
            .registerPacket(NeteaseTextPacket::new,new NeteaseTextSerializer(),9, PacketRecipient.BOTH)
            .deregisterPacket(ResourcePackStackPacket.class)
            .registerPacket(NeteaseResourcePackStackPacket::new,new ResourcePackStackSerializer(),7, PacketRecipient.BOTH)
            .deregisterPacket(SetPlayerInventoryOptionsPacket.class)
            .registerPacket(NeteaseSetPlayerInventoryOptionsPacket::new,new SetPlayerInventoryOptionsSerializer(),307, PacketRecipient.BOTH)
            .deregisterPacket(ContainerClosePacket.class)
            .deregisterPacket(CraftingDataPacket.class)
            .helper(() -> new NeteaseBedrockCodecHelper(ENTITY_DATA, GAME_RULE_TYPES, ITEM_STACK_REQUEST_TYPES, CONTAINER_SLOT_TYPES, PLAYER_ABILITIES, TEXT_PROCESSING_ORIGINS))
            .build();

}

package helper.creeperbox.sdk.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.EntityDataTypeMap;
import org.cloudburstmc.protocol.bedrock.codec.v575.BedrockCodecHelper_v575;
import org.cloudburstmc.protocol.bedrock.codec.v766.BedrockCodecHelper_v766;
import org.cloudburstmc.protocol.bedrock.data.Ability;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryActionData;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.common.util.TypeMap;
import org.cloudburstmc.protocol.common.util.VarInts;
import org.cloudburstmc.protocol.common.util.stream.LittleEndianByteBufInputStream;
import org.cloudburstmc.protocol.common.util.stream.LittleEndianByteBufOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;

public class NeteaseBedrockCodecHelper extends BedrockCodecHelper_v766 {

    // 网易版 item definitions 数量超过默认限制，增加到 4096
    private static final int NETEASE_MAX_ARRAY_SIZE = 4096;

    public NeteaseBedrockCodecHelper(EntityDataTypeMap entityData, TypeMap<Class<?>> gameRulesTypes, TypeMap<ItemStackRequestActionType> stackRequestActionTypes, TypeMap<ContainerSlotType> containerSlotTypes, TypeMap<Ability> abilities, TypeMap<TextProcessingEventOrigin> textProcessingEventOrigins) {
        super(entityData, gameRulesTypes, stackRequestActionTypes, containerSlotTypes, abilities, textProcessingEventOrigins);
    }

    @Override
    public <T> void readArray(ByteBuf buffer, Collection<T> collection, BiFunction<ByteBuf, BedrockCodecHelper, T> reader) {
        readArray(buffer, collection, VarInts::readUnsignedInt, reader, NETEASE_MAX_ARRAY_SIZE);
    }

    @Override
    public <T> void readArray(ByteBuf buffer, Collection<T> collection, ToLongFunction<ByteBuf> lengthReader, BiFunction<ByteBuf, BedrockCodecHelper, T> reader, int maxLength) {
        long length = lengthReader.applyAsLong(buffer);
        // 使用更大的限制
        int limit = Math.max(maxLength, NETEASE_MAX_ARRAY_SIZE);
        if (length > limit) {
            throw new IllegalArgumentException("Tried to read " + length + " elements but maximum is " + limit);
        }
        for (int i = 0; i < length; i++) {
            collection.add(reader.apply(buffer, this));
        }
    }

    @Override
    public void writeString(ByteBuf buffer, String string) {
        if(string == null){
            string = "";
        }
        VarInts.writeUnsignedInt(buffer, ByteBufUtil.utf8Bytes(string));
        buffer.writeCharSequence(string, StandardCharsets.UTF_8);
    }

    @Override
    public void writeItemStackRequest(ByteBuf buffer, ItemStackRequest request) {
        super.writeItemStackRequest(buffer, request);
    }

    @Override
    public void writeItemUse(ByteBuf buffer, InventoryTransactionPacket packet) {
        super.writeItemUse(buffer, packet);
    }

    @Override
    public void writeInventoryActions(ByteBuf buffer, List<InventoryActionData> actions, boolean hasNetworkIds) {
        super.writeInventoryActions(buffer, actions, hasNetworkIds);
    }


    @Override
    public void writeNetItem(ByteBuf buffer, ItemData item) {
        if(item == null){
            VarInts.writeInt(buffer,-1);
            return;
        }
        ItemDefinition definition = item.getDefinition();
        if (isAir(definition)) {
            buffer.writeByte(0);
        } else {
            VarInts.writeInt(buffer, definition.getRuntimeId());
            buffer.writeShortLE(item.getCount());
            VarInts.writeUnsignedInt(buffer, item.getDamage());
            buffer.writeBoolean(item.isUsingNetId());
            if (item.isUsingNetId()) {
                VarInts.writeInt(buffer, item.getNetId());
            }

            VarInts.writeInt(buffer, item.getBlockDefinition() == null ? 0 : item.getBlockDefinition().getRuntimeId());
            ByteBuf userDataBuf = ByteBufAllocator.DEFAULT.ioBuffer();

            try {
                LittleEndianByteBufOutputStream stream = new LittleEndianByteBufOutputStream(userDataBuf);
                Throwable var6 = null;

                try {
                    NBTOutputStream nbtStream = new NBTOutputStream(stream);
                    Throwable var8 = null;

                    try {
                        if (item.getTag() != null) {
                            stream.writeShort(-1);
                            stream.writeByte(1);
                            nbtStream.writeTag(item.getTag());
                        } else {
                            userDataBuf.writeShortLE(0);
                        }

                        String[] canPlace = item.getCanPlace();
                        stream.writeInt(canPlace.length);
                        String[] canBreak = canPlace;
                        int var11 = canPlace.length;

                        int var12;
                        for(var12 = 0; var12 < var11; ++var12) {
                            String aCanPlace = canBreak[var12];
                            stream.writeUTF(aCanPlace);
                        }

                        canBreak = item.getCanBreak();
                        stream.writeInt(canBreak.length);
                        String[] var57 = canBreak;
                        var12 = canBreak.length;

                        for(int var58 = 0; var58 < var12; ++var58) {
                            String aCanBreak = var57[var58];
                            stream.writeUTF(aCanBreak);
                        }

                        if ("minecraft:shield".equals(definition.getIdentifier())) {
                            stream.writeLong(item.getBlockingTicks());
                        }

                        VarInts.writeUnsignedInt(buffer, userDataBuf.readableBytes());
                        buffer.writeBytes(userDataBuf);
                    } catch (Throwable var51) {
                        var8 = var51;
                        throw var51;
                    } finally {
                        if (nbtStream != null) {
                            if (var8 != null) {
                                try {
                                    nbtStream.close();
                                } catch (Throwable var50) {
                                    var8.addSuppressed(var50);
                                }
                            } else {
                                nbtStream.close();
                            }
                        }

                    }
                } catch (Throwable var53) {
                    var6 = var53;
                    throw var53;
                } finally {
                    if (stream != null) {
                        if (var6 != null) {
                            try {
                                stream.close();
                            } catch (Throwable var49) {
                                var6.addSuppressed(var49);
                            }
                        } else {
                            stream.close();
                        }
                    }

                }
            } catch (IOException var55) {
                throw new IllegalStateException("Unable to write item user data", var55);
            } finally {
                userDataBuf.release();
            }
        }
    }

    @Override
    public ItemData readNetItem(ByteBuf buffer) {
        int runtimeId = VarInts.readInt(buffer);
        if(runtimeId == -1){
            return null;
        }
        if (runtimeId == 0) {
            // We don't need to read anything extra.
            return ItemData.AIR;
        }
        ItemDefinition definition = this.itemDefinitions.getDefinition(runtimeId);
        int count = buffer.readUnsignedShortLE();
        int damage = VarInts.readUnsignedInt(buffer);

        boolean hasNetId = buffer.readBoolean();
        int netId = 0;
        if (hasNetId) {
            netId = VarInts.readInt(buffer);
        }

        int blockRuntimeId = VarInts.readInt(buffer);

        NbtMap compoundTag = null;
        long blockingTicks = 0;
        String[] canPlace;
        String[] canBreak;

        ByteBuf buf = buffer.readSlice(VarInts.readUnsignedInt(buffer));
        try (LittleEndianByteBufInputStream stream = new LittleEndianByteBufInputStream(buf);
             NBTInputStream nbtStream = new NBTInputStream(stream)) {
            int nbtSize = stream.readShort();

            if (nbtSize > 0) {
                compoundTag = (NbtMap) nbtStream.readTag();
            } else if (nbtSize == -1) {
                int tagCount = stream.readUnsignedByte();
                if (tagCount != 1) throw new IllegalArgumentException("Expected 1 tag but got " + tagCount);
                compoundTag = (NbtMap) nbtStream.readTag();
            }

            canPlace = new String[stream.readInt()];
            for (int i = 0; i < canPlace.length; i++) {
                canPlace[i] = stream.readUTF();
            }
            canBreak = new String[stream.readInt()];
            for (int i = 0; i < canBreak.length; i++) {
                canBreak[i] = stream.readUTF();
            }

            if (definition != null && BLOCKING_ID.equals(definition.getIdentifier())) {
                blockingTicks = stream.readLong();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read item user data", e);
        }

        if (buf.isReadable()) {
            log.info("Item user data has {} readable bytes left\n{}", buf.readableBytes(), ByteBufUtil.prettyHexDump(buf.readerIndex(0)));
        }

        return ItemData.builder()
                .definition(definition)
                .damage(damage)
                .count(count)
                .tag(compoundTag)
                .canPlace(canPlace)
                .canBreak(canBreak)
                .blockingTicks(blockingTicks)
                .blockDefinition(this.blockDefinitions.getDefinition(blockRuntimeId))
                .usingNetId(hasNetId)
                .netId(netId)
                .build();
    }



}

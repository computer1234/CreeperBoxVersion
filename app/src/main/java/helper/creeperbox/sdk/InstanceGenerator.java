package helper.creeperbox.sdk;

import android.util.Log;

import helper.creeperbox.hook.HopeHookEntrance;
import helper.creeperbox.sdk.network.BlockMap;
import helper.creeperbox.sdk.network.ItemMap;
import helper.creeperbox.sdk.network.codec.NeteaseBedrock;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityItem;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.entity.type.EntityServerPlayer;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;

import helper.creeperbox.utils.ResourceUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

public class InstanceGenerator {

    public static ItemMap itemMap;
    private static BlockMap blockMap;
    private static BedrockCodecHelper helper;

    private static BedrockCodec codec;

    private static int senderSubClientId = 0;

    private static int targetSubClientId = 0;


    private static ByteBuf writeBuf;

    private static final ReentrantLock lock = new ReentrantLock();


    static {
        writeBuf = ByteBufAllocator.DEFAULT.buffer(1024*1024*10);
        codec = NeteaseBedrock.CODEC;
        helper = codec.createHelper();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(ResourceUtil.decryptStream(HopeHookEntrance.class.getClassLoader().getResourceAsStream("assets/encrypt/c.vmp")), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        itemMap = new ItemMap(reader);
        try {
            blockMap = new BlockMap(ResourceUtil.decryptStream(HopeHookEntrance.class.getClassLoader().getResourceAsStream("assets/encrypt/b.vmp")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        helper.setItemDefinitions(itemMap);
        helper.setBlockDefinitions(blockMap);
    }

    public static BedrockPacket generatorPacket(byte[] data){
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        int header = VarInts.readUnsignedInt(buf);
        senderSubClientId = header >> 10 & 3;
        targetSubClientId = header >> 12 & 3;
        return codec.tryDecode(helper, buf, header & 0x3ff);
    }


    public static String getName(byte[] data){
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        int header = VarInts.readUnsignedInt(buf);
        return codec.getPacketDefinition(header & 0x3ff).getSerializer().getClass().getName();
    }

    public static SerializedSkin generatorSkin(byte[] data){
        return helper.readSkin(Unpooled.wrappedBuffer(data));
    }

    public static byte[] decodeItem(ItemData data){
        lock.lock();
        helper.writeItem(writeBuf,data);
        byte[] b = new byte[writeBuf.readableBytes()];
        writeBuf.readBytes(b);
        writeBuf.clear();
        lock.unlock();
        return b;
    }

    public static byte[] decodePacket(BedrockPacket packet) {
        lock.lock();
        writeBuf.clear();  // 确保 buffer 干净
        int header;
        if (packet instanceof UnknownPacket) {
            header = ((UnknownPacket) packet).getPacketId();
        } else {
            header = codec.getPacketDefinition(packet.getClass()).getId();
        }
        header |= header & 1023;
        header |= (senderSubClientId & 3) << 10;
        header |= (targetSubClientId & 3) << 12;
        VarInts.writeUnsignedInt(writeBuf, header);
        int beforeEncode = writeBuf.writerIndex();
        codec.tryEncode(helper, writeBuf, packet);
        int afterEncode = writeBuf.writerIndex();
        byte[] b = new byte[writeBuf.readableBytes()];
        writeBuf.readBytes(b);
        writeBuf.clear();
        lock.unlock();
        return b;
    }


    public static void init(){}
    public static int getPacketID(BedrockPacket packet){
        if (packet instanceof UnknownPacket) {
            return ((UnknownPacket) packet).getPacketId();
        } else {
            return codec.getPacketDefinition(packet.getClass()).getId();
        }
    }



    public static ItemData generatorItemData(byte[] data){
        return helper.readItem(Unpooled.wrappedBuffer(data));
    }


    public static ByteBuf hexStringToByteBuf(String hex) {
        int length = hex.length();
        byte[] bytes = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }

        return Unpooled.wrappedBuffer(bytes);
    }

    public static EntityActor generatorPlayer(long pointer){
        if(pointer == 0) return null;
        EntityActor actor = new EntityActor(pointer);
        if(actor.isPrimaryLocalPlayer()){
            return new EntityLocalPlayer(pointer);
        }
        return new EntityServerPlayer(pointer);
    }


    public static EntityActor generatorEntity(long pointer){
        if(pointer == 0) return null;
        EntityActor actor = new EntityActor(pointer);
        String namespace = actor.getNameSpace();
        if (namespace == null || namespace.isEmpty()) return null;
        if (actor.getEntityID() == 319){
            if(actor.isPrimaryLocalPlayer()){
                return new EntityLocalPlayer(pointer);
            }
            return new EntityServerPlayer(pointer);
        }
        if (namespace.equals("minecraft:item")) return new EntityItem(pointer);
        return actor;
    }

}

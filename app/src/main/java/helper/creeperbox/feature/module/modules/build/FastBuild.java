package helper.creeperbox.feature.module.modules.build;

import static helper.creeperbox.feature.module.modules.build.FastBuild.CHUNK_SIZE;

import android.annotation.SuppressLint;

import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOutputMessage;
import org.cloudburstmc.protocol.bedrock.packet.CommandOutputPacket;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.SettingsCommandPacket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import helper.creeperbox.clickgui.VerifyGUI;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.GameDataComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2i;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCommandRequestPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "导入建筑", category = Category.Build)
public class FastBuild extends Module {

    public static final String PATH = "build.schematic";
    private final NumberValue speed = new NumberValue("速率", this,3000,1000, 10000, 1);
    private final NumberValue multiple = new NumberValue("倍数", this,1,1, 20, 1);

    @Override
     public void onDisable() {
        success = false;
        xOff = 0;
        yOff = 0;
        zOff = 0;
        width = 0;
        height = 0;
        length = 0;
        blocks = null;
        blockData = null;
        currentX = 0;
        currentY = 0;
        currentZ = 0;
        totalBlock = 0;
        playerName = "";
        lastUUID = UUID.randomUUID();
        currentIndex = 0;
        dataList = null;
        watch = null;
        loadedBlock = 0;
    }


    static void executeCommandFast(EntityLocalPlayer player,String command) {
        SettingsCommandPacket packet = new SettingsCommandPacket();
        packet.setCommand(command);
        packet.setSuppressingOutput(true);
        player.sendPacket(packet);
    }

    private static String[] blockMap = {"air","stone","grass","dirt","cobblestone","planks","sapling","bedrock","flowing_water","water","flowing_lava","lava","sand","gravel","gold_ore","iron_ore","coal_ore","log","leaves","sponge","glass","lapis_ore","lapis_block","dispenser","sandstone","noteblock","bed","golden_rail","detector_rail","sticky_piston","cobweb","tallgrass","deadbush","piston","piston_head","wool","piston_extension","dandelion","poppy","brown_mushroom","red_mushroom","gold_block","iron_block","double_stone_slab","stone_slab","brick_block","tnt","bookshelf","mossy_cobblestone","obsidian","torch","fire","monster_spawner","oak_stairs","chest","redstone_wire","diamond_ore","diamond_block","crafting_table","wheat","farmland","furnace","lit_furnace","standing_sign","wooden_door","ladder","rail","stone_stairs","wall_sign","lever","stone_pressure_plate","iron_door","wooden_pressure_plate","redstone_ore","lit_redstone_ore","unlit_redstone_torch","redstone_torch","stone_button","snow_layer","ice","snow","cactus","clay","reeds","jukebox","fence","pumpkin","netherrack","soul_sand","glowstone","portal","lit_pumpkin","cake","unpowered_repeater","powered_repeater","stained_glass","trapdoor","monster_egg","stonebrick","brown_mushroom_block","red_mushroom_block","iron_bars","glass_pane","melon_block","pumpkin_stem","melon_stem","vine","fence_gate","brick_stairs","stone_brick_stairs","mycelium","waterlily","nether_brick","nether_brick_fence","nether_brick_stairs","nether_wart","enchanting_table","brewing_stand","cauldron","end_portal","end_portal_frame","end_stone","dragon_egg","redstone_lamp","lit_redstone_lamp","double_wooden_slab","wooden_slab","cocoa","sandstone_stairs","emerald_ore","ender_chest","tripwire_hook","tripwire","emerald_block","spruce_stairs","birch_stairs","jungle_stairs","command_block","beacon","cobblestone_wall","flower_pot","carrots","potatoes","wooden_button","skull","anvil","trapped_chest","light_weighted_pressure_plate","heavy_weighted_pressure_plate","unpowered_comparator","powered_comparator","daylight_detector","redstone_block","quartz_ore","hopper","quartz_block","quartz_stairs","activator_rail","dropper","stained_hardened_clay","stained_glass_pane","leaves2","log2","acacia_stairs","dark_oak_stairs","slime","barrier","iron_trapdoor","prismarine","sealantern","hay_block","carpet","hardened_clay","coal_block","packed_ice","double_plant","standing_banner","wall_banner","daylight_detector_inverted","red_sandstone","red_sandstone_stairs","double_stone_slab2","stone_slab2","spruce_fence_gate","birch_fence_gate","jungle_fence_gate","dark_oak_fence_gate","acacia_fence_gate","spruce_fence","birch_fence","jungle_fence","dark_oak_fence","acacia_fence_gate","spruce_door","birch_door","jungle_door","acacia_door","dark_oak_door","end_rod","chorus_plant","chorus_flower","purpur_block","purpur_pillar","purpur_stairs","purpur_double_slab","purpur_slab","end_bricks","beetroots","grass_path","end_gateway","repeating_command_block","chain_command_block","frosted_ice","magma","nether_wart_block","red_nether_brick","bone_block","structure_void","observer","white_shulker_box","orange_shulker_box","magenta_shulker_box","light_blue_shulker_box","yellow_shulker_box","lime_shulker_box","pink_shulker_box","gray_shulker_box","light_gray_shulker_box","cyan_shulker_box","purple_shulker_box","blue_shulker_box","brown_shulker_box","green_shulker_box","red_shulker_box","black_shulker_box","white_glazed_terracotta","orange_glazed_terracotta","magenta_glazed_terracotta","light_blue_glazed_terracotta","yellow_glazed_terracotta","lime_glazed_terracotta","pink_glazed_terracotta","gray_glazed_terracotta","light_gray_glazed_terracotta","cyan_glazed_terracotta","purple_glazed_terracotta","blue_glazed_terracotta","brown_glazed_terracotta","green_glazed_terracotta","red_glazed_terracotta","black_glazed_terracotta","concrete","concrete_powder","null","null","structure_block",};
    public final static int CHUNK_SIZE = 64;
    private int xOff;
    private int yOff;
    private int zOff;
    private int width;
    private int height;
    private int length;
    private byte[] blocks;
    private byte[] blockData;
    private int currentX;
    private int currentY;
    private int currentZ;
    private boolean success;
    private int totalBlock;
    private String playerName = "";
    private StopWatch watch = new StopWatch();
    private int currentIndex;
    private List<LargeChunkData> dataList;
    private int loadedBlock;

    public long ONE_HOUR = 60 * 60 * 1000;
    public long ONE_DAY = 24 * ONE_HOUR;
    public long SEVEN_DAYS_ONE_HOUR = 7 * ONE_DAY + ONE_HOUR;

    private int speedValue;

    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;

        // -1 表示永久用户，应该允许使用
        if(CreeperBox.y() != -1 && CreeperBox.y() <= SEVEN_DAYS_ONE_HOUR){
            ModalFormRequestPacket packet = new ModalFormRequestPacket();
            packet.setFormData("{\"type\":\"form\",\"title\":\"导入建筑失败\",\"content\":\"§c有效期小于七天,此功能仅为月卡及以上用户使用\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
            packet.setFormId(0);
            player.receivePacket(packet);
            return;
        }


        try (InputStream fis = new FileInputStream(new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH));
             GZIPInputStream gzipIn = new GZIPInputStream(fis);
             NBTInputStream nbtIn = new NBTInputStream(new DataInputStream(gzipIn))) {

            watch = new StopWatch();
            NbtMap nbt = (NbtMap) nbtIn.readTag();
            width = nbt.getShort("Width");
            height = nbt.getShort("Height");
            length = nbt.getShort("Length");
            blocks = nbt.getByteArray("Blocks");
            blockData = nbt.getByteArray("Data");
            Vec3f startPos = player.getPos();
            xOff = (int) startPos.x;
            yOff = (int) startPos.y;
            zOff = (int) startPos.z;
            watch.reset();
            currentX = 0;
            currentY = 0;
            currentZ = 0;
            success = true;
            loadedBlock = 0;
            playerName = player.getNameTag();
            speedValue = this.speed.getCurrentValue().intValue() * this.multiple.getCurrentValue().intValue();

            int startChunkX = Math.floorDiv(xOff, CHUNK_SIZE);
            int endChunkX = Math.floorDiv(xOff + width - 1, CHUNK_SIZE);
            int startChunkZ = Math.floorDiv(zOff, CHUNK_SIZE);
            int endChunkZ = Math.floorDiv(zOff + length - 1, CHUNK_SIZE);


            dataList = new ArrayList<>();
            totalBlock = 0;
            for (int chunkX = startChunkX; chunkX <= endChunkX; chunkX++) {
                for (int chunkZ = startChunkZ; chunkZ <= endChunkZ; chunkZ++) {

                    LargeChunkData chunkData = new LargeChunkData(chunkX, chunkZ);

                    boolean hasBlock = false;

                    int chunkMinX = chunkData.largeChunkX * CHUNK_SIZE;
                    int chunkMaxX = chunkMinX + CHUNK_SIZE - 1;
                    int chunkMinZ = chunkData.largeChunkZ * CHUNK_SIZE;
                    int chunkMaxZ = chunkMinZ + CHUNK_SIZE - 1;

                    for (int x = Math.max(xOff, chunkMinX); x <= Math.min(xOff + width - 1, chunkMaxX); x++) {
                        for (int z = Math.max(zOff, chunkMinZ); z <= Math.min(zOff + length - 1, chunkMaxZ); z++) {
                            for (int y = yOff; y < yOff + height; y++) {
                                int localX = x - xOff;
                                int localY = y - yOff;
                                int localZ = z - zOff;
                                int index = localY * width * length + localZ * width + localX;

                                if (index >= 0 && index < blocks.length && index < blockData.length) {
                                    int blockId = blocks[index] & 0xff;
                                    if(blockId > 0 && blockId < blockMap.length){
                                        totalBlock++;
                                        hasBlock = true;
                                    }
                                }
                            }
                        }
                    }

                    if(hasBlock){
                        dataList.add(chunkData);
                    }
                }
            }

            if(totalBlock == 0){
                ModalFormRequestPacket packet = new ModalFormRequestPacket();
                File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH);
                packet.setFormData("{\"type\":\"form\",\"title\":\"文件读取失败\",\"content\":\"§c导入文件为空,请去 "+file.getAbsolutePath()+ "修改\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
                packet.setFormId(0);
                player.receivePacket(packet);
                success = false;
                return;
            }

            if(!dataList.isEmpty()){
                Vec2i center = dataList.get(0).getChunkCenter();
                executeCommandFast(player, String.format("execute as @a[name=\"%s\"] at @s run tp @s %d %d %d", playerName, center.x, 100, center.y));
                testForBlocks(player,dataList.get(0));
            }

        } catch (FileNotFoundException e) {
            ModalFormRequestPacket packet = new ModalFormRequestPacket();
            File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH);
            packet.setFormData("{\"type\":\"form\",\"title\":\"文件读取失败\",\"content\":\"§c导入文件不存在,请去 "+file.getAbsolutePath()+ "添加文件\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
            packet.setFormId(0);
            player.receivePacket(packet);
        } catch (Exception e){
            ModalFormRequestPacket packet = new ModalFormRequestPacket();
            File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH);
            packet.setFormData("{\"type\":\"form\",\"title\":\"文件读取失败\",\"content\":\"§c导入读取失败,请去 "+file.getAbsolutePath()+ "修改\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
            packet.setFormId(0);
            player.receivePacket(packet);
            e.printStackTrace();
        }
    }



    private UUID lastUUID = UUID.randomUUID();
    private LargeChunkData currentChunk;


    private void testForBlocks(EntityLocalPlayer player, LargeChunkData chunkData) {
        currentChunk = chunkData;
        int chunkMinX = chunkData.largeChunkX * CHUNK_SIZE;
        int chunkMaxX = chunkMinX + CHUNK_SIZE - 1;
        int chunkMinZ = chunkData.largeChunkZ * CHUNK_SIZE;
        int chunkMaxZ = chunkMinZ + CHUNK_SIZE - 1;
        NeteaseCommandRequestPacket packet = new NeteaseCommandRequestPacket();
        packet.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, lastUUID, "", 0L));
        packet.setInternal(false);
        packet.setVersion(36);
        packet.setHasExtra(true);
        packet.setCommand(String.format("/testforblocks %d -64 %d %d 319 %d %d -64 %d",chunkMinX,chunkMinZ,chunkMaxX,chunkMaxZ,chunkMinX,chunkMinZ));
        player.sendPacket(packet);
    }


    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event){
        if(event.getPacket() instanceof CommandOutputPacket
                && currentChunk != null){
            CommandOutputPacket packet = (CommandOutputPacket) event.getPacket();
            if(packet.getCommandOriginData().getUuid().equals(lastUUID)){
                EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                if(player != null){
                    event.setCancelled();
                    for(CommandOutputMessage msg : packet.getMessages()){
                        if(msg.getMessageId().equals("commands.compare.tooManyBlocks")){
                            currentChunk.loaded = true;
                            return;
                        }
                    }
                    Vec2i center = currentChunk.getChunkCenter();
                    executeCommandFast(player, String.format("execute as @a[name=\"%s\"] at @s run tp @s %d %d %d", playerName, center.x, 100, center.y));
                    testForBlocks(player,currentChunk);
                }
            }
        }
    }


    @SubscribeEvent
    public void onMove(PacketSendEvent event) {
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket) {
            ((NeteasePlayerAuthInputPacket) event.getPacket()).setCameraDeparted(true);
            if (!((NeteasePlayerAuthInputPacket) event.getPacket()).getInputData().contains(PlayerAuthInputData.HANDLE_TELEPORT))
            {
                event.setCancelled();
            }
        }
    }


    @SuppressLint("DefaultLocale")
    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (!success || GameDataComponent.tick < 20*10) return;

        if (watch.finished(1000)) {
            executeCommandFast(event.getPlayer(),String.format("execute as @a[name=\"%s\"] at @s run title @a actionbar §b[CreeperBox]§d速度:§f%d/blocks §e剩余方块:§f%d blocks",playerName,speedValue,Math.max(totalBlock-loadedBlock,0)));
            int count = 0;
            SettingsCommandPacket packet = new SettingsCommandPacket();
            packet.setSuppressingOutput(true);

            while (true) {

                if(!dataList.get(currentIndex).loaded){
                    return;
                }

                LargeChunkData chunkData = dataList.get(currentIndex);
                int chunkMinX = chunkData.largeChunkX * CHUNK_SIZE;
                int chunkMaxX = chunkMinX + CHUNK_SIZE - 1;
                int chunkMinZ = chunkData.largeChunkZ * CHUNK_SIZE;
                int chunkMaxZ = chunkMinZ + CHUNK_SIZE - 1;

                int startY = (currentY != 0) ? currentY : yOff;
                int startX = (currentX != 0 && startY == currentY) ? currentX : Math.max(xOff, chunkMinX);
                int startZ = (currentZ != 0 && startY == currentY) ? currentZ : Math.max(zOff, chunkMinZ);

                for (int y = startY; y < yOff + height; y++) {
                    int xStart = (y == startY) ? startX : Math.max(xOff, chunkMinX);

                    for (int x = xStart; x <= Math.min(xOff + width - 1, chunkMaxX); x++) {
                        int zStart = (y == startY && x == startX) ? startZ : Math.max(zOff, chunkMinZ);

                        for (int z = zStart; z <= Math.min(zOff + length - 1, chunkMaxZ); z++) {
                            int localX = x - xOff;
                            int localY = y - yOff;
                            int localZ = z - zOff;

                            int index = localY * width * length + localZ * width + localX;

                            if (index >= 0 && index < blocks.length && index < blockData.length) {
                                int blockId = blocks[index] & 0xff;
                                int data = blockData[index] & 0xff;
                                if (blockId > 0 && blockId < blockMap.length) {
                                    String name = blockMap[blockId];
                                    if (index - 188 <= 5 && index - 188 >= 0) {
                                        name = "fence";
                                        data = index - 188;
                                    }
                                    if (index == 3 && data == 2) {
                                        name = "podzol";
                                    }

                                    String command = String.format("setblock %d %d %d %s %d",
                                            x, y, z, name, data);
                                    packet.setCommand(command);
                                    event.getPlayer().sendPacket(packet);

                                    currentX = x;
                                    currentY = y;
                                    currentZ = z;

                                    loadedBlock++;
                                    if (++count > speedValue) {
                                        watch.reset();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

                currentIndex++;
                currentX = 0;
                currentY = 0;
                currentZ = 0;


                if (currentIndex >= dataList.size()) {
                    success = false;
                    return;
                }

                Vec2i center = dataList.get(currentIndex).getChunkCenter();
                executeCommandFast(event.getPlayer(), String.format("execute as @a[name=\"%s\"] at @s run tp @s %d %d %d", playerName, center.x, 100, center.y));
                testForBlocks(event.getPlayer(),dataList.get(currentIndex));
            }
        }

    }


}

class LargeChunkData {
    public int largeChunkX;
    public int largeChunkZ;
    public boolean loaded = false;


    public LargeChunkData(int largeChunkX, int largeChunkZ) {
        this.largeChunkX = largeChunkX;
        this.largeChunkZ = largeChunkZ;
    }

    public Vec2i getChunkCenter(){
        return new Vec2i(largeChunkX*CHUNK_SIZE+CHUNK_SIZE/2,largeChunkZ*CHUNK_SIZE+CHUNK_SIZE/2);
    }
}

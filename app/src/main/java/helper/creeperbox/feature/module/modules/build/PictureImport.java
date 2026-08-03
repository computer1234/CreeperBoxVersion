package helper.creeperbox.feature.module.modules.build;

import static helper.creeperbox.feature.module.modules.build.PictureImport.CHUNK_SIZE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.google.gson.Gson;

import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOutputMessage;
import org.cloudburstmc.protocol.bedrock.packet.CommandOutputPacket;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.SettingsCommandPacket;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2i;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCommandRequestPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.utils.mc.StopWatch;


@ModuleInfo(name = "导入像素画", category = Category.Build)
public class PictureImport extends Module {

    public static final String PATH = "pic.png";
    private final NumberValue widthValue = new NumberValue("宽", this,128,16, 512, 1);
    private final NumberValue heightValue = new NumberValue("高", this,128,16, 512, 1);
    private final BooleanValue zValue = new BooleanValue("竖直导入",this,false);

    public long ONE_HOUR = 60 * 60 * 1000;
    public long ONE_DAY = 24 * ONE_HOUR;
    public long SEVEN_DAYS_ONE_HOUR = 7 * ONE_DAY + ONE_HOUR;

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

    private String blockJson =
            "[{\"blockName\":\"minecraft:black_wool\",\"R\":21,\"G\":17,\"B\":17},{\"blockName\":\"minecraft:blue_wool\",\"R\":52,\"G\":65,\"B\":162},{\"blockName\":\"minecraft:brown_wool\",\"R\":85,\"G\":55,\"B\":33},{\"blockName\":\"minecraft:cyan_wool\",\"R\":42,\"G\":100,\"B\":124},{\"blockName\":\"minecraft:green_wool\",\"R\":57,\"G\":76,\"B\":30},{\"blockName\":\"minecraft:gray_wool\",\"R\":59,\"G\":59,\"B\":59},{\"blockName\":\"minecraft:light_blue_wool\",\"R\":60,\"G\":185,\"B\":222},{\"blockName\":\"minecraft:light_gray_wool\",\"R\":174,\"G\":178,\"B\":178},{\"blockName\":\"minecraft:lime_wool\",\"R\":67,\"G\":174,\"B\":56},{\"blockName\":\"minecraft:magenta_wool\",\"R\":189,\"G\":97,\"B\":197},{\"blockName\":\"minecraft:orange_wool\",\"R\":219,\"G\":123,\"B\":59},{\"blockName\":\"minecraft:pink_wool\",\"R\":207,\"G\":129,\"B\":149},{\"blockName\":\"minecraft:purple_wool\",\"R\":117,\"G\":55,\"B\":169},{\"blockName\":\"minecraft:red_wool\",\"R\":142,\"G\":48,\"B\":46},{\"blockName\":\"minecraft:white_wool\",\"R\":241,\"G\":241,\"B\":241},{\"blockName\":\"minecraft:yellow_wool\",\"R\":196,\"G\":184,\"B\":46},{\"blockName\":\"minecraft:hardened_clay\",\"R\":152,\"G\":95,\"B\":69},{\"blockName\":\"minecraft:black_terracotta\",\"R\":37,\"G\":23,\"B\":16},{\"blockName\":\"minecraft:blue_terracotta\",\"R\":74,\"G\":59,\"B\":91},{\"blockName\":\"minecraft:brown_terracotta\",\"R\":77,\"G\":52,\"B\":36},{\"blockName\":\"minecraft:clay\",\"R\":165,\"G\":169,\"B\":185},{\"blockName\":\"minecraft:cyan_terracotta\",\"R\":87,\"G\":92,\"B\":92},{\"blockName\":\"minecraft:green_terracotta\",\"R\":76,\"G\":84,\"B\":44},{\"blockName\":\"minecraft:gray_terracotta\",\"R\":59,\"G\":44,\"B\":36},{\"blockName\":\"minecraft:light_gray_terracotta\",\"R\":133,\"G\":102,\"B\":93},{\"blockName\":\"minecraft:light_blue_terracotta\",\"R\":112,\"G\":108,\"B\":138},{\"blockName\":\"minecraft:lime_terracotta\",\"R\":103,\"G\":117,\"B\":52},{\"blockName\":\"minecraft:magenta_terracotta\",\"R\":149,\"G\":87,\"B\":108},{\"blockName\":\"minecraft:orange_terracotta\",\"R\":164,\"G\":87,\"B\":39},{\"blockName\":\"minecraft:pink_terracotta\",\"R\":160,\"G\":77,\"B\":78},{\"blockName\":\"minecraft:purple_terracotta\",\"R\":118,\"G\":70,\"B\":86},{\"blockName\":\"minecraft:red_terracotta\",\"R\":142,\"G\":60,\"B\":46},{\"blockName\":\"minecraft:white_terracotta\",\"R\":209,\"G\":177,\"B\":161},{\"blockName\":\"minecraft:yellow_terracotta\",\"R\":184,\"G\":131,\"B\":34},{\"blockName\":\"minecraft:snow\",\"R\":238,\"G\":255,\"B\":255},{\"blockName\":\"minecraft:gold_block\",\"R\":255,\"G\":249,\"B\":80},{\"blockName\":\"minecraft:iron_block\",\"R\":233,\"G\":233,\"B\":233},{\"blockName\":\"minecraft:birch_planks\",\"R\":215,\"G\":203,\"B\":141},{\"blockName\":\"minecraft:jungle_planks\",\"R\":177,\"G\":128,\"B\":92},{\"blockName\":\"minecraft:spruce_planks\",\"R\":120,\"G\":88,\"B\":54},{\"blockName\":\"minecraft:oak_planks\",\"R\":188,\"G\":152,\"B\":98},{\"blockName\":\"minecraft:sandstone\",\"R\":221,\"G\":212,\"B\":165},{\"blockName\":\"minecraft:stone\",\"R\":127,\"G\":127,\"B\":127}]";

    @Override
    public void onEnable() {

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if (player == null) return;

        // -1 表示永久用户，应该允许使用
        if(CreeperBox.y() != -1 && CreeperBox.y() <= SEVEN_DAYS_ONE_HOUR){
            ModalFormRequestPacket packet = new ModalFormRequestPacket();
            packet.setFormData("{\"type\":\"form\",\"title\":\"导入图片失败\",\"content\":\"§c有效期小于七天,此功能仅为月卡及以上用户使用\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
            packet.setFormId(0);
            player.receivePacket(packet);
            return;
        }

        File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH);
        if(!file.exists()){
            ModalFormRequestPacket packet = new ModalFormRequestPacket();
            packet.setFormData("{\"type\":\"form\",\"title\":\"图片读取失败\",\"content\":\"§c导入文件不存在,请去 "+file.getAbsolutePath()+ "添加文件\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
            packet.setFormId(0);
            player.receivePacket(packet);
            return;
        }
        try {
            watch = new StopWatch();

            WoolData[] list = strToList(blockJson);
            int[][][] matrix = getPixelMatrix(file.getAbsolutePath(),widthValue.getCurrentValue().intValue(),heightValue.getCurrentValue().intValue());
            int[][] result = getResultColor(list,matrix);

            Vec3f startPos = player.getPos();
            xOff = (int) startPos.x;
            yOff = (int) startPos.y;
            zOff = (int) startPos.z;
            boolean isZ = zValue.getCurrentValue();

            if(isZ){
                width = 1;
                height = result.length;
                length = result[0].length;
            }else {
                width = result.length;
                height = 1;
                length = result[0].length;
            }

            watch.reset();
            currentX = 0;
            currentY = 0;
            currentZ = 0;
            success = true;
            playerName = player.getNameTag();
            totalBlock = 0;
            blocks = new String[width*height*length];
            currentIndex = 0;

            if(isZ){
                for(int i = height - 1;i >= 0 ;i--){
                    for(int j = length - 1 ;j >= 0;j--){
                        int y = height-i-1;
                        int z = length-j-1;
                        int index = y * width * length + z * width;
                        blocks[index] = list[result[i][j]].blockName;
                    }
                }
            }else {
                for(int i = width - 1;i >= 0 ;i--){
                    for(int j = length - 1 ;j >= 0;j--){
                        int x = i;
                        int z = length-j-1;
                        int index = z * width + x;
                        blocks[index] = list[result[i][j]].blockName;
                    }
                }
            }


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
                                String blockId = blocks[index];
                                if(blockId!=null){
                                    totalBlock++;
                                    hasBlock = true;
                                }
                            }
                        }
                    }

                    if(hasBlock){
                        dataList.add(chunkData);
                    }
                }
            }

            if(!dataList.isEmpty()){
                Vec2i center = dataList.get(0).getChunkCenter();
                executeCommandFast(player, String.format("execute as @a[name=\"%s\"] at @s run tp @s %d %d %d", playerName, center.x, 100, center.y));
                testForBlocks(player,dataList.get(0));
            }

        }catch (Exception e) {
//            CreeperBox.INSTANCE.activity.runOnUiThread(()->{
//                Toast.makeText(CreeperBox.INSTANCE.activity.getBaseContext(),e.toString(),Toast.LENGTH_LONG).show();
//            });
            ModalFormRequestPacket packet = new ModalFormRequestPacket();
            packet.setFormData("{\"type\":\"form\",\"title\":\"图片读取失败\",\"content\":\"§c导入读取失败,格式错误,请去 "+file.getAbsolutePath()+ "修改\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
            packet.setFormId(0);
            player.receivePacket(packet);
            e.printStackTrace();
        }
    }


    private UUID lastUUID = UUID.randomUUID();
    private LargeChunkData currentChunk;
    public final static int CHUNK_SIZE = 64;
    private int xOff;
    private int yOff;
    private int zOff;
    private int width;
    private int height;
    private int length;
    private String[] blocks;
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
            if (!((NeteasePlayerAuthInputPacket) event.getPacket()).getInputData().contains(PlayerAuthInputData.HANDLE_TELEPORT) && success)
            {
                event.setCancelled();
            }
        }
    }





    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (!success || GameDataComponent.tick < 20*10) return;

        if (watch.finished(1000)) {
            executeCommandFast(event.getPlayer(),String.format("execute as @a[name=\"%s\"] at @s run title @a actionbar §b[CreeperBox]§d速度:§f3000/blocks §e剩余方块:§f%d blocks",playerName,Math.max(totalBlock-loadedBlock,0)));
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

                            if (index >= 0 && index < blocks.length) {
                                String blockId = blocks[index];
                                if (blockId != null) {
                                    @SuppressLint("DefaultLocale") String command = String.format("setblock %d %d %d %s",
                                            x, y, z, blockId);
                                    packet.setCommand(command);
                                    event.getPlayer().sendPacket(packet);

                                    currentX = x;
                                    currentY = y;
                                    currentZ = z;

                                    loadedBlock++;
                                    if (++count > 3000) {
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



    static void executeCommandFast(EntityLocalPlayer player,String command) {
        SettingsCommandPacket packet = new SettingsCommandPacket();
        packet.setCommand(command);
        packet.setSuppressingOutput(true);
        player.sendPacket(packet);
    }



    public static int[][][] getPixelMatrix(String filePath,int width,int height) {
        Bitmap bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeFile(filePath),
                width,
                height,
                true
        );

        if (bitmap == null) {
            return null;
        }

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        int[][][] matrix = new int[height][width][3];

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixels[index++];
                matrix[i][j][0] = (pixel >> 16) & 0xFF;
                matrix[i][j][1] = (pixel >> 8) & 0xFF;
                matrix[i][j][2] = pixel & 0xFF;
            }
        }
        bitmap.recycle();
        return matrix;
    }



    private static int compareColor(WoolData[] list,int[] RGB){
        if(list.length <= 0) {
            return -1;
        }
        int R = RGB[0];
        int G = RGB[1];
        int B = RGB[2];
        int index = 0;
        double min = Math.sqrt((list[0].R - R)*(list[0].R - R) + (list[0].G - G)*(list[0].G - G) + (list[0].B - B)*(list[0].B - B));
        double temp;
        for(int i = 1;i < list.length;i++){
            if((temp = Math.sqrt((list[i].R - R)*(list[i].R - R) + (list[i].G - G)*(list[i].G - G) + (list[i].B - B)*(list[i].B - B))) < min){
                min = temp;
                index = i;
            }
        }
        return index;
    }


    public static int[][] getResultColor(WoolData[] list,int[][][] matrix){
        int height = matrix.length;
        int width = matrix[0].length;
        int[][] result = new int[height][width];
        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                result[i][j] = compareColor(list,matrix[i][j]);
            }
        }
        return result;
    }


    public static WoolData[] strToList(String str){
        Gson gson = new Gson();
        return gson.fromJson(str,WoolData[].class);
    }



}


class WoolData {
    String blockName;
    int R;
    int G;
    int B;
}

